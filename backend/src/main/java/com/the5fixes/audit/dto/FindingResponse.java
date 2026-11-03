package com.the5fixes.audit.dto;

import com.the5fixes.audit.Type.Severity;

public record FindingResponse(String auditId,
                              Severity severity,
                              String title,
                              int lcpSavingsMs,
                              int fcpSavingsMs,
                              int effortMinutes) {
}