package com.the5fixes.audit.Repository;

import com.the5fixes.audit.Entity.AuditJob;
import com.the5fixes.audit.Entity.AuditResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuditResultRepository extends JpaRepository<AuditResult, UUID> {
    Optional<AuditResult> findByJobId(UUID jobId);
}
