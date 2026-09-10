# adm-soler

API de administração de obras (Spring Boot 4.1, Java 25, Maven).
`./mvnw test` roda tudo. Perfis: `h2` (dev/teste) e `postgres`.

## Arquitetura

Clean Architecture em três camadas. **A dependência só aponta para dentro:**

```
infra  ──▶  application  ──▶  core
```

- `core` não conhece ninguém.
- `application` conhece `core`.
- `infra` conhece as duas.

O padrão de referência deste projeto é o repositório `personal_finance_app`
(`~/projetos/personal_finance_app`). Em dúvida de convenção, olhe lá primeiro.

Antes de dar um passo como concluído, verifique as fronteiras:

```bash
grep -rn "import org.springframework\|import jakarta\|adm_soler.application\|adm_soler.infra" \
  src/main/java/com/buccodev/adm_soler/core/          # deve não retornar nada
grep -rn "import org.springframework\|adm_soler.infra" \
  src/main/java/com/buccodev/adm_soler/application/   # deve não retornar nada
```

### `core` — regra de negócio, zero framework

- **Nenhum import de Spring ou Jakarta.** Nem `@Entity`, nem `@Component`, nem
  `@NotNull`. A única anotação aceita é `@Override`.
- Entidades **sem setter**. Construtor privado, criação só por factory
  (`Entidade.create(...)`) e reidratação por `Entidade.restore(...)`.
  Mudança de estado é método de intenção — `rename()`, `settle()`,
  `applyTransaction()` — não `setNome()`.
- Invariantes numa única `private static void validate(...)` chamada do
  construtor, para que nenhum objeto inválido exista.
- Relação entre agregados **por id** (`ownerId`, `userId`), não por grafo de
  objetos. Grafo dentro do domínio é o que força `JOIN FETCH` em cascata e
  problema de N+1 na infra.
- `core/exception`: `DomainException` **abstrata**, uma exceção por agregado
  (`InvalidUserException`, ...), construtor privado e **factories estáticas**
  que carregam a mensagem:

  ```java
  throw InvalidUserException.blankName();
  throw InvalidTransactionException.categoryNotOwnedByUser(categoryId, userId);
  ```

  Sem string de erro solta no meio da regra.
- `core/repository`: interfaces apenas, com uma `Repository<T, ID>` genérica
  (`save`, `findById`, `delete(T)`) que as específicas estendem. Nada de JPA,
  `Page` ou `Pageable` aqui.
- `core/security`: ports de segurança (`PasswordHasher`, `TokenService`),
  falando em tipos de domínio e devolvendo `Optional`.

### `application` — casos de uso

- Use cases são **POJO**: sem `@Component`/`@Service`. Dependências entram por
  construtor e o registro como bean fica em `infra/config/UseCaseConfiguration`.
- **Anotação de Spring é proibida. Bean Validation (`jakarta.validation`) é
  permitida e esperada nos DTOs de request** — `@NotBlank`, `@Email`, `@Size`,
  `@Positive`. É o contrato de entrada, validado com `@Valid` no controller e
  traduzido em 400 pelo `GlobalExceptionHandler`.
- `application/dto`: records de dados. **Nenhum método de mapeamento** —
  sem `toDomain`/`fromDomain` dentro do record.
- `application/mapper`: classe `final` + construtor privado, métodos estáticos
  `toDomain(...)` e `toResponse(...)`. É a única casa da conversão DTO ↔ domínio.
- `application/exception`: `ApplicationException` abstrata, uma exceção por caso
  de erro, também com factories estáticas
  (`UserNotFoundException.withId(id)`, `ForbiddenException.notOwner()`).
- Autorização por dono do recurso vive aqui, não no controller: um
  `AccessGuard` (`requireOwner`, `requireOwnerOrAdmin`, `requireAdmin`) e o use
  case recebendo o usuário autenticado como parâmetro.

### `infra` — detalhe

- `infra/rest/controllers`: fino. Recebe request, delega ao use case, devolve
  `ResponseEntity`. Sem regra de negócio.
- `infra/rest/entities`: entidades JPA, separadas do domínio. Enum de
  persistência é **próprio** da entidade e convertido por `valueOf(name())` —
  não reaproveite o enum de domínio no `@Enumerated`.
- **A conversão domínio ↔ entidade JPA mora dentro do adapter**, como métodos
  estáticos. Não crie um pacote de mapper na infra: mapper é conceito da
  application.
- `infra/rest/adapters`: implementam os contratos de `core/repository`.
- `infra/config`: `@Configuration` com os `@Bean` dos use cases e o bootstrap
  inicial, sempre pelos ports (`UserRepository`, `PasswordEncoderPort`), nunca
  tocando JPA direto.
- Erro HTTP com `ProblemDetail` (RFC 7807), não `Map` montado à mão:
  não encontrado → 404, já existe → **409**, credencial inválida → 401,
  sem permissão → 403, `DomainException` → 400,
  `DataIntegrityViolationException` → 409.

### Leitura paginada e N+1

Os mappers navegam associações `@ManyToOne`, então `findAll(Pageable)` puro
gera um SELECT por linha. Toda listagem paginada usa JPQL com `JOIN FETCH` e
`countQuery` separada:

```java
@Query(value = """
        SELECT p FROM ProjectJpa p
        LEFT JOIN FETCH p.client c
        LEFT JOIN FETCH c.address
        """,
        countQuery = "SELECT COUNT(p) FROM ProjectJpa p")
Page<ProjectJpa> findAllWithRelations(Pageable pageable);
```

**Só associação *-para-um* entra no `JOIN FETCH`.** Coleção multiplica linha, o
Hibernate cai para paginação em memória (`HHH90003004`) e o `LIMIT` deixa de
existir. Coleção fica LAZY e o mapper passa `null`.

Para conferir o custo real de uma leitura, ligue
`spring.jpa.properties.hibernate.generate_statistics=true` e leia
`SessionFactory.getStatistics().getPrepareStatementCount()`. Uma página bem
resolvida faz 2 queries (fetch + count), independente do tamanho.

## Testes

- Pacote de teste **espelha o de produção**: `core/domain/UserTest`,
  `application/usecase/UserUseCaseTest`, `application/mapper/UserMapperTest`,
  `infra/rest/controllers/UserControllerIntegrationTest`.
- Use case montado com `new` no `@BeforeEach`, repositórios e ports mockados.
- Teste de controller é integração de verdade: sobe o contexto, faz login,
  usa o token. Um `AbstractControllerIntegrationTest` guarda os helpers.
- Toda invariante de domínio tem teste — é o código mais barato de testar,
  porque não precisa de mock nenhum.

## Backlog de alinhamento

Divergências conhecidas em relação ao padrão acima, ainda não aplicadas:

- Domínio tem ~90 setters e relação por grafo de objeto (`Project.client`,
  `Restaurant.address`); os mappers usam `applyTo(entidade, request)` em vez de
  método de intenção.
- `DomainException` é concreta e as mensagens são strings inline; falta uma
  exceção por agregado com factories.
- `ApplicationException` tem só `ResourceNotFoundException`/`BadRequestException`
  genéricas, com mensagem inline; falta 409 para conflito.
- `GlobalExceptionHandler` devolve `Map`, não `ProblemDetail`.
- Ports de segurança estão em `application/gateway`; a referência os coloca em
  `core/security`.
- Autorização é só `@PreAuthorize` no controller; não há modelo de dono do
  recurso nem `AccessGuard`.
- `infra/rest/mappers` ainda existe como pacote separado do adapter.
- `core/repository` repete `save`/`findById`/`deleteById`/`existsById` em cada
  interface; falta a `Repository<T, ID>` genérica.
- Schema por `ddl-auto`; a referência usa Flyway.
- Sem springdoc/OpenAPI.
- Só os DTOs de `auth` têm Bean Validation; os demais requests não validam nada.
- **Access token e refresh token são intercambiáveis** — o `JwtAuthenticationFilter`
  aceita um refresh token como credencial de acesso. A referência separa por um
  claim `type` (`access`/`refresh`). É a divergência com impacto de segurança.

Duas coisas em que este projeto está **à frente** da referência e devem ser
preservadas:

- `Persistable` + `markAsExisting()` nas entidades JPA, que evita o SELECT antes
  do INSERT que o `save()` da referência paga em toda escrita.
- Paginação de primeira classe (`core/pagination` com `PageQuery`/`PageResult`),
  que a referência não tem.
