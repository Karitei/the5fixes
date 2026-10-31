package com.the5fixes.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.the5fixes.audit.Entity.AuditResult;
import com.the5fixes.audit.Type.RawAudit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class ReportParser {

    public List<RawAudit> extractAudits(JsonNode root){
        List<RawAudit> audits = new ArrayList<>();

        JsonNode auditsNode = root.path("audits");
        Iterator<Map.Entry<String, JsonNode>> fields = auditsNode.fields();

        while (fields.hasNext()) {
            JsonNode audit = fields.next().getValue();
            JsonNode savings = audit.path("metricSavings");

            if (savings.isMissingNode()) {
                continue;
            }

            audits.add(new RawAudit(
                    audit.path("id").asText(),
                    savings.path("LCP").asInt(),
                    savings.path("FCP").asInt()
            ));
        }

        return audits;
    }
}
