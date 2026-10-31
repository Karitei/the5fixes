package com.the5fixes.audit.Type;

import com.the5fixes.audit.Entity.AuditResult;

import java.util.List;

public record LighthouseRecord()  {
    public static int performance;
    public static int accessibility;
    public static int bestPractices;
    public static int seo;
    public static double loadTimeSeconds;
    public static long pageSizeBytes ;
    public static List<RawAudit> rawAudits;

    public static void setPerformance(int performance) {
        LighthouseRecord.performance = performance;
    }

    public static void setAccessibility(int accessibility) {
        LighthouseRecord.accessibility = accessibility;
    }

    public static void setBestPractices(int bestPractices) {
        LighthouseRecord.bestPractices = bestPractices;
    }

    public static void setSeo(int seo) {
        LighthouseRecord.seo = seo;
    }

    public static void setLoadTimeSeconds(double loadTimeSeconds) {
        LighthouseRecord.loadTimeSeconds = loadTimeSeconds;
    }

    public static void setPageSizeBytes(long pageSizeBytes) {
        LighthouseRecord.pageSizeBytes = pageSizeBytes;
    }

    public static int getPerformance() {
        return performance;
    }

    public static int getAccessibility() {
        return accessibility;
    }

    public static int getBestPractices() {
        return bestPractices;
    }

    public static int getSeo() {
        return seo;
    }

    public static double getLoadTimeSeconds() {
        return loadTimeSeconds;
    }

    public static long getPageSizeBytes() {
        return pageSizeBytes;
    }

    public static List<RawAudit> getRawAudits() {
        return rawAudits;
    }

    public static void setRawAudits(List<RawAudit> rawAudits) {
        LighthouseRecord.rawAudits = rawAudits;
    }
}
