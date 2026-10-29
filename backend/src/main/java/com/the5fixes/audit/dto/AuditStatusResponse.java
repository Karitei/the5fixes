package com.the5fixes.audit.dto;

import com.the5fixes.audit.Type.JobStatus;
import jakarta.annotation.Nullable;

import java.util.UUID;

public record AuditStatusResponse(UUID jobId, JobStatus status,
                                  AuditScores scores, @Nullable String errorMessage) {


}
