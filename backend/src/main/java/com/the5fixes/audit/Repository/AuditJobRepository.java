package com.the5fixes.audit.Repository;

import com.the5fixes.audit.Entity.AuditJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuditJobRepository extends JpaRepository<AuditJob, UUID> {
    Optional<AuditJob> findById(UUID id);

}
