-- ============================================================================
-- V3 — Audit tables
-- Adding tabes for audit.
-- ============================================================================

CREATE TABLE audit_job (
     id                 UUID         PRIMARY KEY,
     email              VARCHAR(255) ,
     url                VARCHAR(2048) NOT NULL,
     normalized_url     VARCHAR(2048) NOT NULL,
     status             VARCHAR(20)  NOT NULL,
     error_message      VARCHAR(1000) ,
     created_at         TIMESTAMPTZ  NOT NULL,
     updated_at         TIMESTAMPTZ  NOT NULL,
     completed_at       TIMESTAMPTZ ,
     created_by         VARCHAR(255),
     updated_by         VARCHAR(255),
     version            BIGINT       NOT NULL DEFAULT 0
     CONSTRAINT     ck_audit_status CHECK (status IN ('PENDING', 'RUNNING', 'DONE', 'FAILED'))
);

CREATE TABLE audit_result  (
    id                      UUID PRIMARY KEY,
    job_id                  UUID NOT NULL UNIQUE,
    performance_score       INTEGER,
    accessibility_score     INTEGER,
    best_practices_score    INTEGER,
    seo_score               INTEGER,
    load_time_seconds       DOUBLE PRECISION,
    page_size_bytes         BIGINT,
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_results_job FOREIGN KEY (job_id) REFERENCES audit_job(id)
);