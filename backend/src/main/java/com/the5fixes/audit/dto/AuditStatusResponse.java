package com.the5fixes.audit.dto;

import com.the5fixes.audit.Entity.AuditFinding;
import com.the5fixes.audit.Type.JobStatus;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public record AuditStatusResponse(UUID jobId,
                                  JobStatus status,
                                  AuditScores scores,
                                  @Nullable List<FindingResponse>  auditFindings,
                                  @Nullable String errorMessage) {


}
