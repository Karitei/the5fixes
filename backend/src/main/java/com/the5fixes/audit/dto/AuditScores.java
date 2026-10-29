package com.the5fixes.audit.dto;

import jakarta.validation.constraints.NotBlank;

import static com.the5fixes.audit.Type.LighthouseRecord.*;

public record AuditScores(  @NotBlank int       performance,
                            @NotBlank int       accessibility,
                            @NotBlank int       bestPractices,
                            @NotBlank int       seo,
                            @NotBlank Double    loadTimeSeconds,
                            @NotBlank long      pageSize) {}


