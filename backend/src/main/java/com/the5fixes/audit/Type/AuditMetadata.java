package com.the5fixes.audit.Type;

import java.util.Map;

public class AuditMetadata {
    public static final Map<String, ComplianceDefinition> COMPLIANCE = Map.ofEntries(
            Map.entry("color-contrast", new ComplianceDefinition(
                    "Some text is hard to read against its background",
                    Severity.HIGH,
                    120)),
            Map.entry("agent-accessibility-tree", new ComplianceDefinition(
                    "AI assistants cannot navigate your site reliably",
                    Severity.HIGH,
                    240)),
            Map.entry("label-content-name-mismatch", new ComplianceDefinition(
                    "Some buttons announce a different name than they display",
                    Severity.MEDIUM,
                    60)),
            Map.entry("select-name", new ComplianceDefinition(
                    "Some dropdown menus have no accessible name",
                    Severity.MEDIUM,
                    30)),
            Map.entry("ard-schema", new ComplianceDefinition(
                    "Your site does not describe its content in a machine-readable way",
                    Severity.MEDIUM,
                    120)),
            Map.entry("third-party-cookies", new ComplianceDefinition(
                    "Your site sets third-party cookies that browsers are phasing out",
                    Severity.MEDIUM,
                    120)),
            Map.entry("errors-in-console", new ComplianceDefinition(
                    "Your site throws JavaScript errors in the browser",
                    Severity.MEDIUM,
                    180)),
            Map.entry("image-size-responsive", new ComplianceDefinition(
                    "Some images are displayed at the wrong size",
                    Severity.MEDIUM,
                    90)),
            Map.entry("llms-txt", new ComplianceDefinition(
                    "Your llms.txt file does not follow the standard",
                    Severity.LOW,
                    60)),
            Map.entry("meta-description", new ComplianceDefinition(
                    "Some pages are missing a meta description",
                    Severity.LOW,
                    15)),
            Map.entry("robots-txt", new ComplianceDefinition(
                    "Your robots.txt file returns an error",
                    Severity.LOW,
                    30)),
            Map.entry("bf-cache", new ComplianceDefinition(
                    "Going back reloads the whole page instead of restoring it",
                    Severity.LOW,
                    240))
    );

}
