package com.the5fixes.audit.Entity;

import com.the5fixes.audit.Type.AuditEventType;
import com.the5fixes.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Append-only business audit trail. Never updated, never deleted.
 * This is what lets you answer "who changed this, and when?" — the first question
 * asked in any incident, audit or client dispute.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_created_at", columnList = "created_at"),
        @Index(name = "idx_audit_event_type", columnList = "event_type"),
        @Index(name = "idx_audit_actor", columnList = "actor")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private AuditEventType eventType;

    /** Email or "system" — kept as a string so it survives user deletion. */
    @Column(nullable = false, length = 255)
    private String actor;

    @Column(name = "target_type", length = 60)
    private String targetType;

    @Column(name = "target_id", length = 60)
    private String targetId;

    @Column(length = 500)
    private String details;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    public AuditLog(AuditEventType eventType, String actor, String targetType, String targetId,
                    String details, String ipAddress) {
        this.eventType = eventType;
        this.actor = actor;
        this.targetType = targetType;
        this.targetId = targetId;
        this.details = details;
        this.ipAddress = ipAddress;
    }
}
