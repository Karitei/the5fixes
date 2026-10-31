package com.the5fixes.audit.Type;

import java.util.UUID;

public record Finding(String id, String title ,  Severity severity,
                      int lcpSavingsMs, int fcpSavingsMs, int effortMinutes) {
}
