package com.the5fixes.audit.Entity;

import com.the5fixes.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class AuditResult extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "job_id")
    private AuditJob job;

    @Column(nullable = false, updatable = false)
    @Min(0)
    @Max(100)
    private int performanceScore;

    @Column(nullable = false, updatable = false)
    @Min(0)
    @Max(100)
    private int accessibilityScore;

    @Column(nullable = false, updatable = false)
    @Min(0)
    @Max(100)
    private int bestPracticesScore;

    @Column(nullable = false, updatable = false)
    @Min(0)
    @Max(100)
    private int seoScore;

    @Column(nullable = false,updatable = false)
    private Double loadTimeSeconds;

    @Column(nullable = false, updatable = false)
    private long pageSizeBytes;

    @Column(nullable = false,updatable = false)
    private String reportPath;

    public AuditResult(int performanceScore, int accessibilityScore, int bestPracticesScore,
                       int seoScore, Double loadTimeSeconds, long pageSizeBytes) {
        this.performanceScore = performanceScore;
        this.accessibilityScore = accessibilityScore;
        this.bestPracticesScore = bestPracticesScore;
        this.seoScore = seoScore;
        this.loadTimeSeconds = loadTimeSeconds;
        this.pageSizeBytes = pageSizeBytes;
    }


    public AuditResult() {

    }

    public void setJob(AuditJob job) {
        this.job = job;
    }

    public AuditJob getJob() {
        return job;
    }

    public int getPerformanceScore() {
        return performanceScore;
    }

    public int getAccessibilityScore() {
        return accessibilityScore;
    }

    public int getBestPracticesScore() {
        return bestPracticesScore;
    }

    public int getSeoScore() {
        return seoScore;
    }

    public Double getLoadTimeSeconds() {
        return loadTimeSeconds;
    }

    public long getPageSizeBytes() {
        return pageSizeBytes;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setPerformanceScore(int performanceScore) {
        this.performanceScore = performanceScore;
    }

    public void setAccessibilityScore(int accessibilityScore) {
        this.accessibilityScore = accessibilityScore;
    }

    public void setBestPracticesScore(int bestPracticesScore) {
        this.bestPracticesScore = bestPracticesScore;
    }

    public void setSeoScore(int seoScore) {
        this.seoScore = seoScore;
    }

    public void setLoadTimeSeconds(Double loadTimeSeconds) {
        this.loadTimeSeconds = loadTimeSeconds;
    }

    public void setPageSizeBytes(long pageSizeBytes) {
        this.pageSizeBytes = pageSizeBytes;
    }
}
