package com.the5fixes.audit.Repository;

import com.the5fixes.audit.Entity.AuditFinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditFindingRepository extends JpaRepository<AuditFinding, UUID> {
    List<AuditFinding> findByJobIdOrderByDisplayOrder(UUID jobId);
}
