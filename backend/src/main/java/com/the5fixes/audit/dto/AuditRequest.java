package com.the5fixes.audit.dto;

import jakarta.validation.constraints.NotBlank;

public record AuditRequest(@NotBlank String url) {}



