# adm-soler

API de administração de obras (Spring Boot 4.1, Java 25, Maven).
`./mvnw test` roda tudo. Perfis: `h2` (dev) e `postgres`. Schema por Flyway.

## Arquitetura

Clean Architecture em três camadas. **A dependência só aponta para dentro:**

```
infra  ──▶  application  ──▶  core
```

- `core` não conhece ninguém.
- `application` conhece `core`.
- `infra` conhece as duas.

O padrão de referência é o repositório `personal_finance_app`
(`~/projetos/personal_finance_app`). Em dúvida de convenção, olhe lá primeiro.

Antes de dar um passo como concluído, verifique as fronteiras:

```bash
grep -rn "import org.springframework\|import jakarta\|adm_soler.application\|adm_soler.infra" \
  src/main/java/com/buccodev/adm_soler/core/          # deve não retornar nada
grep -rn "import org.springframework\|adm_soler.infra" \
  src/main/java/com/buccodev/adm_soler/application/   # deve não retornar nada
```

### `core` — regra de negócio, zero framework

```
core/domain      entidades
core/exception   DomainException + uma por agregado
core/repository  Repository<T,ID> e os contratos por agregado
core/security    PasswordHasher, TokenService
```

- **Nenhum import de Spring ou Jakarta.** A única anotação aceita é `@Override`.
- Entidades **sem setter**. Construtor privado, criação por `X.create(...)`,
  reidratação por `X.restore(...)`. Mutação só por método de intenção —
  `update(...)`, `fits(...)`, `valuePerEmployee(...)` — nunca `setNome()`.
- Invariantes numa única `private static void validate(...)` chamada do
  construtor **e** do `update`, para que nenhum objeto inválido exista e um
  update recusado não deixe o objeto meio alterado.
- **Relação entre agregados por id** (`addressId`, `clientId`, `projectId`),
  nunca por grafo de objetos. Foi o grafo que gerava N+1 e cascata de
  `JOIN FETCH`; sem ele o problema não existe.
- Dado que depende de outro agregado entra como **parâmetro**, não como campo:
  `Restaurant.valuePerEmployee(int employeeCount)`, `Accommodation.fits(int)`.
- `core/exception`: `DomainException` **abstrata**, uma exceção por agregado
  (`InvalidClientException`, `InvalidPeriodException`, ...), construtor privado
  e **factories estáticas** que carregam a mensagem:

  ```java
  throw InvalidClientException.blankName();
  throw InvalidPeriodException.startAfterEnd(start, end);
  ```

  Sem string de erro solta no meio da regra.
- `core/repository`: `Repository<T, ID>` genérica com `save`, `findById`,
  `findAll(PageQuery)`, `delete(T)` e `existsById`. **A paginação são records
  aninhados nela** (`Repository.PageQuery`, `Repository.PageResult`) — não é um
  pacote à parte. `delete` recebe a entidade, não o id.
- `core/security`: ports de segurança, falando em tipos de domínio e devolvendo
  `Optional`. Não existe pacote `gateway`.

### `application` — casos de uso

```
application/dto       XRequestDto / XResponseDto
application/exception ApplicationException + uma por caso de erro
application/mapper    XMapper (final, estático)
application/usecase   XUseCase (POJO)
```

- Use cases são **POJO**: sem `@Component`/`@Service`. Dependências por
  construtor; o registro como bean fica em `infra/rest/config/UseCaseConfig`.
- **Anotação de Spring é proibida. Bean Validation (`jakarta.validation`) é
  esperada nos DTOs de request** — `@NotBlank`, `@Email`, `@Size`, `@Positive`.
  É o contrato de entrada, validado com `@Valid` no controller.
- DTOs de response são **achatados**: `addressId`, não um `address` aninhado.
- `application/mapper`: classe `final` + construtor privado, estáticos
  `toDomain(...)` e `toResponseDto(...)`. Única casa da conversão DTO ↔ domínio.
- `application/exception`: `ApplicationException` abstrata, uma exceção por caso
  de erro, com factories: `ClientNotFoundException.withId(id)`,
  `UserAlreadyExistsException.withEmail(email)`,
  `InvalidCredentialsException.badLogin()`.
- Senha: o caso de uso aplica o `PasswordHasher` **antes** de montar o domínio.
  O domínio nunca vê senha em claro; o mínimo de caracteres é `@Size` no DTO.

### `infra` — detalhe

```
infra/rest/adapters        implementam core/repository
infra/rest/config          UseCaseConfig, OpenApiConfig
infra/rest/controllers     controllers + GlobalExceptionHandler
infra/rest/entities        XEntity (JPA)
infra/rest/jpa_repository  interfaces Spring Data
infra/rest/security        SecurityConfig, JwtService, filtro, hasher, bootstrap
```

- Controller é fino: recebe request, delega, devolve `ResponseEntity`.
- Entidades JPA são `XEntity`, tabela `xxx_tb`, ctor `protected` + ctor completo.
  **FK é coluna escalar `UUID`, não `@ManyToOne`** — o domínio referencia por id,
  então não há associação a mapear e nem lazy loading para explodir.
- **A conversão domínio ↔ entidade mora dentro do adapter**, como métodos
  estáticos. Não crie pacote de mapper na infra: mapper é conceito da application.
- Erro HTTP com `ProblemDetail` (RFC 7807): não encontrado → 404, já existe →
  **409**, credencial inválida → 401, sem permissão → 403, `DomainException` →
  400, `DataIntegrityViolationException` → 409.
- **O principal do Security é o `User` de domínio.** O filtro JWT resolve o
  usuário pelo `UserRepository` e o controller pode recebê-lo com
  `@AuthenticationPrincipal User`. Não há `UserDetailsService` nem
  `AuthenticationManager` — o `UserDetailsServiceAutoConfiguration` é excluído
  no yaml, e o login confere o hash no próprio caso de uso.
- **Access token e refresh token são distinguidos por um claim `type`.** Um
  refresh token não autentica requisição e um access token não renova sessão.
  Se você mexer no `JwtService`, mantenha isso — tem teste cobrindo.
- Autorização é por matcher no `SecurityConfig` (escrita → ADMIN, listagem de
  usuários → ADMIN, resto → autenticado). Quando existir recurso com dono,
  a regra de propriedade vai para um `AccessGuard` na application, recebendo o
  usuário autenticado como parâmetro — não para `@PreAuthorize`.
- Schema por **Flyway** (`db/migration/V1__create_tables.sql`), com
  `ddl-auto: validate` em todo perfil: se a entidade e a migration divergirem,
  o contexto não sobe. Mudança de schema é migration nova, nunca editar a V1.

### Paginação e custo de leitura

Listagens usam `Repository.PageQuery` e devolvem `Repository.PageResult`, que o
`PageMapper` converte em `PageResponseDto`. Como não há associação JPA, uma
página custa 2 queries (a página + o count), independente do tamanho.

Se algum dia voltar a existir `@ManyToOne` numa entidade, a listagem precisa de
JPQL com `JOIN FETCH` e `countQuery` separada — e **só associação *-para-um***,
porque coleção multiplica linha, o Hibernate cai para paginação em memória
(`HHH90003004`) e o `LIMIT` deixa de existir.

Para medir de verdade: ligue
`spring.jpa.properties.hibernate.generate_statistics=true` e leia
`SessionFactory.getStatistics().getPrepareStatementCount()`.

## Testes

- Pacote de teste **espelha o de produção**: `core/domain/ClientTest`,
  `application/usecase/ClientUseCaseTest`, `application/mapper/ClientMapperTest`,
  `infra/rest/controllers/ClientControllerIntegrationTest`.
- Use case montado com `new` no `@BeforeEach`, repositórios e ports mockados.
- Teste de controller é integração de verdade: sobe o contexto, faz login como
  admin, usa o token. Os helpers ficam no `AbstractControllerIntegrationTest`
  (`adminAccessToken`, `userAccessToken`, `createAddress`, `createClient`,
  `createProject`).
- Toda invariante de domínio tem teste — é o código mais barato de testar,
  porque não precisa de mock nenhum.
- Um `update` recusado deve ser testado também pelo que **não** mudou.

## Pendências

- Não existe recurso com dono (todo dado é global, o papel decide o acesso).
  Quando existir, criar `application/security/AccessGuard` e passar o usuário
  autenticado aos casos de uso, como na referência.
- Não há alocação de funcionário a restaurante/acomodação/projeto na API, então
  `valuePerEmployee` sai sempre zerado e `Accommodation.fits` nunca é exercitado
  em produção. Quando o vínculo existir, a contagem vem do repositório e entra
  como parâmetro (o ponto de costura já está pronto).
- `RestaurantUseCase.NO_EMPLOYEES_ASSIGNED` é o zero temporário desse rateio.
