package com.the5fixes.audit.Entity;

import com.the5fixes.audit.Type.JobStatus;
import com.the5fixes.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
public class AuditJob extends BaseEntity {

    @Email
    @Column(nullable = true)
    private String email;
    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String normalizedUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(nullable = true)
    private String errorMessage;


    @Column(name = "completedAt", nullable = true, updatable = false)
    private Instant completedAt;
    

    public AuditJob() {

    }
    public AuditJob(String url, String normalizedUrl) {
        this.url = url;
        this.normalizedUrl = normalizedUrl;   // déjà propre
    }

    public String getEmail() {
        return email;
    }

    public String getUrl() {
        return url;
    }

    public String getNormalizedUrl() {
        return normalizedUrl;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNormalizedUrl(String normalizedUrl) {
        this.normalizedUrl = normalizedUrl;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
