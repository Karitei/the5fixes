package com.the5fixes.audit;

import com.the5fixes.audit.Type.Finding;
import com.the5fixes.audit.Type.RawAudit;
import com.the5fixes.audit.Type.Severity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindingEngineTest {

    @Test
    void shouldPassFindings(){

        List<RawAudit> audits = List.of(
                new RawAudit("render-blocking-insight", 2150, 1150),
                new RawAudit("document-latency-insight", 1950, 150),
                new RawAudit("unused-javascript", 120, 1150),
                new RawAudit("cache-insight", 950, 1150),
                new RawAudit("modern-http-insight", 120, 200)
        );

        List<Finding> actual = FindingEngine.Properties(audits);

        List<Finding> expected = List.of(
                new Finding("render-blocking-insight",
                        "Stylesheets and scripts delay your first display",
                        Severity.CRITICAL,
                        2150,
                        1150),

                new Finding("document-latency-insight",
                        "Your server responds slowly, or redirects too many times",
                        Severity.HIGH,1950,
                        150),
                new Finding("cache-insight",
                        "Returning visitors re-download files that never change",
                        Severity.MEDIUM,
                        950,
                        1150),

                new Finding("unused-javascript",
                        "Your site downloads JavaScript it never runs",
                        Severity.LOW,
                        120,
                        1150)

                );

        assertEquals(expected,actual);
    }
}
