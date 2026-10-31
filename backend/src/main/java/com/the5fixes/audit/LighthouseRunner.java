package com.the5fixes.audit;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.the5fixes.audit.Type.LighthouseRecord;
import com.the5fixes.audit.Type.RawAudit;
import com.the5fixes.common.AuditFailedException;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class LighthouseRunner {
    private final String scriptPath;
    private final int timeoutSeconds;
    private final ObjectMapper mapper;
    private final ReportParser reportParser;

    public LighthouseRunner(@Value("${the5fixes.lighthouse.script}") String scriptPath,
                            @Value("${the5fixes.lighthouse.timeout-seconds:180}") int timeoutSeconds, ObjectMapper mapper, ReportParser reportParser) {
        this.scriptPath = scriptPath;
        this.timeoutSeconds = timeoutSeconds;
        this.mapper = mapper;
        this.reportParser = reportParser;
    }

    public LighthouseRecord run(String url) {
        Path output = null;
        Path log = null;
        try {
            output = Files.createTempFile("lh-report-", ".json");
            log = Files.createTempFile("lh-log-", ".txt");

            ProcessBuilder pb = new ProcessBuilder(
                    "node", scriptPath, url,
                    "--output=json",
                    "--output-path=" + output.toAbsolutePath(),
                    "--only-categories=performance,accessibility,best-practices,seo",
                    "--chrome-flags=--headless=new",
                    "--quiet"
            );
            pb.redirectErrorStream(true);
            pb.redirectOutput(log.toFile());

            Process process = pb.start();

            if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new AuditFailedException("Lighthouse a dépassé " + timeoutSeconds + " s");
            }
            if (process.exitValue() != 0) {
                String details = Files.readString(log);
                throw new AuditFailedException("Lighthouse a échoué : " + details);
            }

            //Extract auditFindings
            List<RawAudit> rawAudits = parseFindings(output);

            return parse(output, rawAudits);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuditFailedException("Interrompu");
        } catch (IOException e) {
            throw new AuditFailedException("Erreur d'exécution : " + e.getMessage());
        } finally {
            deleteQuietly(output);
            deleteQuietly(log);
        }


    }

    private LighthouseRecord parse(Path output, List<RawAudit> rawAudits) throws IOException  {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(output.toFile());

        JsonNode categories = root
                .path("categories");

        JsonNode audit = root
                .path("audits");

        double performance = categories
                .path("performance")
                .path("score")
                .asDouble() * 100;

        double accessibility = categories
                .path("accessibility")
                .path("score")
                .asDouble() * 100;

        double bestPractice = categories
                .path("best-practices")
                .path("score")
                .asDouble() * 100;

        double seo = categories
                .path("seo")
                .path("score")
                .asDouble() * 100;

        double loadTimeSeconds = audit
                .path("largest-contentful-paint")
                .path("numericValue")
                .asDouble() / 1000;

        double pageSizeBytes = audit
                .path("total-byte-weight")
                .path("numericValue")
                .asDouble() ;

        LighthouseRecord result = new LighthouseRecord();

        result.setPerformance((int) performance);
        result.setAccessibility((int) accessibility);
        result.setBestPractices((int) bestPractice);
        result.setSeo((int) seo);
        result.setLoadTimeSeconds(Math.round(loadTimeSeconds * 100.0) / 100.0);
        result.setPageSizeBytes((long) pageSizeBytes);
        result.setRawAudits(rawAudits);

        return result;
    }

    private List<RawAudit> parseFindings(Path output) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(output.toFile());

        return reportParser.extractAudits(root);
    }

    private void deleteQuietly(Path path) {
        if (path == null) return;
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // Not able to delete file
        }
    }




}
