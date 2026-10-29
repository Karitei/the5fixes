package com.the5fixes.audit.dto;

import java.util.UUID;


public record AuditResponse(UUID jobId, com.the5fixes.audit.Type.JobStatus status) {}
