package com.the5fixes.audit.Controller;

import com.the5fixes.audit.Entity.AuditLog;
import com.the5fixes.audit.Repository.AuditLogRepository;
import com.the5fixes.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public PageResponse<AuditLogView> list(
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return PageResponse.from(auditLogRepository.findAll(pageable), AuditLogView::from);
    }

    public record AuditLogView(String id, String eventType, String actor, String targetType,
                               String targetId, String details, String createdAt) {
        static AuditLogView from(AuditLog log) {
            return new AuditLogView(
                    log.getId() == null ? null : log.getId().toString(),
                    log.getEventType().name(),
                    log.getActor(),
                    log.getTargetType(),
                    log.getTargetId(),
                    log.getDetails(),
                    log.getCreatedAt() == null ? null : log.getCreatedAt().toString());
        }
    }
}
