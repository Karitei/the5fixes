package com.the5fixes.audit.Type;

public enum AuditCheckMetadata {
    RENDER_BLOCKING(
            "render-blocking-insight",
            "Stylesheets and scripts delay your first display",
            180
    ),

    DOCUMENT_LATENCY(
            "document-latency-insight",
            "Your server responds slowly, or redirects too many times",
            240
    ),

    UNUSED_JAVASCRIPT(
            "unused-javascript",
            "Your site downloads JavaScript it never runs",
            360
    ),

    CACHE(
            "cache-insight",
            "Returning visitors re-download files that never change",
            90
    ),

    IMAGE_DELIVERY(
            "image-delivery-insight",
            "Your images are heavier than they need to be",
            180
    ),

    MODERN_HTTP(
            "modern-http-insight",
            "Your server isn't using the modern HTTP protocol",
            60
    ),

    FONT_DISPLAY(
            "font-display-insight",
            "Your text stays invisible while fonts load",
            30
    ),

    UNUSED_CSS(
            "unused-css-rules",
            "Your site downloads CSS it never applies",
            180
    );

    private final String id;
    private final String description;
    private final int effort;

    AuditCheckMetadata(String id, String description, int effort) {
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

    public int getEffort() {
        return effort;
    }
}
