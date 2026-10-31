package com.the5fixes.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.the5fixes.audit.Type.RawAudit;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportParserTest {

    private final ReportParser parser = new ReportParser();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldExtractNineAuditsFromScotiabankReport() throws Exception {
        JsonNode root;
        try (InputStream in = getClass().getResourceAsStream("/test5.json")) {
            assertNotNull(in, "test5.json introuvable dans src/test/resources");
            root = mapper.readTree(in);
        }

        List<RawAudit> audits = parser.extractAudits(root);

        assertEquals(9, audits.size());
        assertTrue(audits.contains(new RawAudit("document-latency-insight", 1150, 1150)));
        assertTrue(audits.contains(new RawAudit("font-display-insight", 0, 900)));
    }

    /** Sans fichier : vérifie que les audits sans gain sont ignorés. */
    @Test
    void shouldIgnoreAuditsWithoutSavings() throws JsonProcessingException {
        JsonNode root = mapper.readTree("""
                {"audits": {
                    "is-on-https":   {"id": "is-on-https", "score": 1},
                    "cache-insight": {"id": "cache-insight", "metricSavings": {"LCP": 1250, "FCP": 0}}
                }}
                """);

        List<RawAudit> audits = parser.extractAudits(root);

        assertEquals(1, audits.size());
        assertEquals(new RawAudit("cache-insight", 1250, 0), audits.get(0));
    }

}
