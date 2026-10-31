package com.the5fixes.audit;

import com.the5fixes.audit.Type.Finding;
import com.the5fixes.audit.Type.RawAudit;
import com.the5fixes.audit.Type.Severity;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class FindingEngineTest {

    @Test
    void shouldPassFindings(){

        List<RawAudit> audits = List.of(
                new RawAudit("render-blocking-insight", 2150, 1150),
                new RawAudit("document-latency-insight", 1950, 150),
                new RawAudit("unused-javascript", 120, 1150),
                new RawAudit("cache-insight", 950, 1150),
                new RawAudit("modern-http-insight", 120, 200),
                new RawAudit("image-delivery-insight", 3000, 1150),
                new RawAudit("modern-http-insight", 500, 200)
        );

        List<Finding> actual = FindingEngine.Properties(audits);

        List<Finding> expected = List.of(
                new Finding("image-delivery-insight",
                        "Your images are heavier than they need to be",
                        Severity.CRITICAL,
                        3000,
                        1150,
                        3),
                new Finding("render-blocking-insight",
                        "Stylesheets and scripts delay your first display",
                        Severity.CRITICAL,
                        2150,
                        1150,
                        3),

                new Finding("document-latency-insight",
                        "Your server responds slowly, or redirects too many times",
                        Severity.HIGH,1950,
                        150,
                        3),
                new Finding("cache-insight",
                        "Returning visitors re-download files that never change",
                        Severity.MEDIUM,
                        950,
                        1150,
                        3),

                new Finding("modern-http-insight",
                        "Your server isn't using the modern HTTP protocol",
                        Severity.MEDIUM,
                        500,
                        200,
                        3)
                );

        assertEquals(expected,actual);
    }
}
