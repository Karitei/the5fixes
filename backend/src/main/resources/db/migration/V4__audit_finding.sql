CREATE TABLE audit_finding (
    id                  UUID PRIMARY KEY,
    job_id              UUID NOT NULL,
    audit_id            VARCHAR(100) NOT NULL,
    severity            VARCHAR(25) NOT NULL,
    title               VARCHAR(500) NOT NULL,
    lcp_savings_ms      INTEGER NOT NULL,
    fcp_savings_ms      INTEGER NOT NULL,
    effort_hours        INTEGER NOT NULL,
    how_to_fix          VARCHAR(500),
    display_order       INTEGER NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL,
    updated_at          TIMESTAMPTZ  NOT NULL,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    version             BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uq_finding_job_audit UNIQUE (job_id, audit_id),
    CONSTRAINT fk_finding_job FOREIGN KEY (job_id) REFERENCES audit_job(id)
);

CREATE INDEX idx_finding_job ON audit_finding(job_id);

