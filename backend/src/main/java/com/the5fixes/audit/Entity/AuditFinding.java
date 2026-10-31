package com.the5fixes.audit.Entity;

import com.the5fixes.audit.Type.Severity;
import com.the5fixes.common.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class AuditFinding extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id")
    private AuditJob job;

    @Column(nullable = false)
    private String auditId;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer lcpSavingsMs;

    @Column(nullable = false)
    private  Integer fcpSavingsMs;

    @Column(nullable = false)
    private Integer effortMinutes;

    @Column(nullable = true)
    private String howToFix;

    @Column(nullable = false)
    private Integer displayOrder;

    public AuditJob getJob() {
        return job;
    }

    public String getAuditId() {
        return auditId;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getTitle() {
        return title;
    }

    public Integer getLcpSavingsMs() {
        return lcpSavingsMs;
    }

    public Integer getFcpSavingsMs() {
        return fcpSavingsMs;
    }

    public Integer getEffortMinutes() {
        return effortMinutes;
    }

    public String getHowToFix() {
        return howToFix;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setJob(AuditJob job) {
        this.job = job;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLcpSavingsMs(Integer lcpSavingsMs) {
        this.lcpSavingsMs = lcpSavingsMs;
    }

    public void setFcpSavingsMs(Integer fcpSavingsMs) {
        this.fcpSavingsMs = fcpSavingsMs;
    }

    public void setEffortMinutes(Integer effortMinutes) {
        this.effortMinutes = effortMinutes;
    }

    public void setHowToFix(String howToFix) {
        this.howToFix = howToFix;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
