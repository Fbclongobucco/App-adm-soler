-- ============================================================================
-- Migracao de seguranca (perfil postgres)
--
-- O perfil postgres usa hibernate.ddl-auto=validate, entao este script precisa
-- ser aplicado a mao ANTES do primeiro deploy com autenticacao. Sem ele a
-- aplicacao nao sobe: falta a tabela refresh_tokens e users.email passa a ser
-- obrigatorio.
--
-- ATENCAO - senhas existentes: antes desta versao as senhas eram gravadas em
-- texto puro. Agora o login compara com BCrypt, entao nenhuma senha antiga vai
-- funcionar. Depois de rodar este script, redefina a senha de cada usuario
-- (via PATCH/PUT em /api/v1/users/{id} autenticado como ADMIN) ou crie um admin
-- novo por BOOTSTRAP_ADMIN_EMAIL / BOOTSTRAP_ADMIN_PASSWORD e refaca os acessos.
-- ============================================================================

-- Sessoes revogaveis. O valor em claro do refresh token nunca e gravado:
-- token_hash guarda o SHA-256 dele.
create table refresh_tokens (
    id             uuid         not null,
    user_id        uuid         not null,
    token_hash     varchar(128) not null,
    expires_at     timestamp    not null,
    created_at     timestamp    not null,
    revoked_at     timestamp,
    revoked_reason varchar(20),
    constraint pk_refresh_tokens primary key (id),
    constraint uk_refresh_tokens_token_hash unique (token_hash),
    constraint fk_refresh_tokens_user foreign key (user_id) references users (id) on delete cascade
);

-- Usado para revogar todas as sessoes de um usuario de uma vez.
create index idx_refresh_tokens_user_id on refresh_tokens (user_id);

-- users.email virou a credencial de login: obrigatorio e unico.
-- Ajuste os registros sem email antes de aplicar a restricao.
update users
   set email = 'sem-email-' || id || '@invalido.local'
 where email is null or email = '';

alter table users alter column email set not null;
alter table users add constraint uk_users_email unique (email);
