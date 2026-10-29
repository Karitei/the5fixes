-- ============================================================================
-- V1 — initial schema
-- Conventions: UUID primary keys, timestamptz in UTC, snake_case, explicit FKs.
-- ============================================================================

CREATE TABLE users (
    id              UUID         PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(100) NOT NULL,
    full_name       VARCHAR(150) NOT NULL,
    role            VARCHAR(20)  NOT NULL,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('USER', 'MANAGER', 'ADMIN'))
);

-- Case-insensitive uniqueness: "Bob@x.com" and "bob@x.com" are the same account.
CREATE UNIQUE INDEX uq_users_email_lower ON users (LOWER(email));

CREATE TABLE refresh_tokens (
    id          UUID         PRIMARY KEY,
    token       VARCHAR(128) NOT NULL,
    user_id     UUID         NOT NULL,
    expires_at  TIMESTAMPTZ  NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    version     BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uq_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);

CREATE TABLE audit_logs (
    id          UUID         PRIMARY KEY,
    event_type  VARCHAR(40)  NOT NULL,
    actor       VARCHAR(255) NOT NULL,
    target_type VARCHAR(60),
    target_id   VARCHAR(60),
    details     VARCHAR(500),
    ip_address  VARCHAR(45),
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    version     BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_audit_created_at ON audit_logs (created_at DESC);
CREATE INDEX idx_audit_event_type ON audit_logs (event_type);
CREATE INDEX idx_audit_actor      ON audit_logs (actor);
