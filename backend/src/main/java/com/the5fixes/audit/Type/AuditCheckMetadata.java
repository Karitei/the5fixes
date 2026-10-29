package com.the5fixes.audit.Type;

public enum AuditCheckMetadata {
    RENDER_BLOCKING(
            "render-blocking-insight",
            "Stylesheets and scripts delay your first display",
            3.0
    ),

    DOCUMENT_LATENCY(
            "document-latency-insight",
            "Your server responds slowly, or redirects too many times",
            3.0
    ),

    UNUSED_JAVASCRIPT(
            "unused-javascript",
            "Your site downloads JavaScript it never runs",
            3.0
    ),

    CACHE(
            "cache-insight",
            "Returning visitors re-download files that never change",
            3.0
    ),

    IMAGE_DELIVERY(
            "image-delivery-insight",
            "Your images are heavier than they need to be",
            3.0
    ),

    MODERN_HTTP(
            "modern-http-insight",
            "Your server isn't using the modern HTTP protocol",
            3.0
    ),

    FONT_DISPLAY(
            "font-display-insight",
            "Your text stays invisible while fonts load",
            3.0
    ),

    UNUSED_CSS(
            "unused-css-rules",
            "Your site downloads CSS it never applies",
            3.0
    );

    private final String id;
    private final String description;
    private final double effort;

    AuditCheckMetadata(String id, String description, double effort) {
        this.id = id;
        this.description = description;
        this.effort = effort;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getEffort() {
        return effort;
    }
}
