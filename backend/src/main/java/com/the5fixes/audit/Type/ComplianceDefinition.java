package com.the5fixes.audit.Type;

public record ComplianceDefinition(String title,
                                   Severity severity,
                                   int effortMinutes) {
}
