package com.the5fixes.audit.Service;

import com.the5fixes.audit.Type.AuditEventType;
import com.the5fixes.audit.Entity.AuditLog;
import com.the5fixes.audit.Repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(AuditEventType type, String actor, String targetType, String targetId,
                       String details, String ipAddress) {
        try {
            repository.save(new AuditLog(type, actor, targetType, targetId, details, ipAddress));
        } catch (Exception ex) {
            // Auditing must never break the actual request.
            log.error("Failed to write audit record for {}", type, ex);
        }
    }
}
