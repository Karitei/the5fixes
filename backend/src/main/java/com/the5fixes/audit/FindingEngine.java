package com.the5fixes.audit;

import com.the5fixes.audit.Type.*;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class FindingEngine {
        private final List<RawAudit> audits;

        public FindingEngine(List<RawAudit> audits) {
            this.audits = audits;
        }

        private static String getFindingDescription(RawAudit audit ){
            return Arrays.stream(AuditCheckMetadata.values())
                    .filter(metadata -> metadata.getId().equals(audit.id()))
                    .map(AuditCheckMetadata::getDescription)
                    .findFirst()
                    .orElse("");
        }
        private static int getFindingEffort(RawAudit audit){
            return Arrays.stream(AuditCheckMetadata.values())
                    .filter(metadata -> metadata.getId().equals(audit.id()))
                    .map(AuditCheckMetadata::getEffort)
                    .findFirst()
                    .orElse( 0);
        }
        private static Severity severityFor(RawAudit audit) {
            int lcp = audit.lcpSavingsMs();
            if (lcp >= 2000) return Severity.CRITICAL;
            if (lcp >= 1000) return Severity.HIGH;
            if (lcp >= 500)  return Severity.MEDIUM;
            if (lcp >= 250)  return Severity.LOW;
            if (audit.fcpSavingsMs() >= 250) return Severity.LOW;
            return null;
        }

        private static List<Finding> prioritiseCompliance(List<RawAudit> audits) {
            return audits.stream()
                    .filter(a -> AuditMetadata.COMPLIANCE.containsKey(a.id()))
                    .filter(a -> a.score() < 0.9)
                    .map(a -> {
                        ComplianceDefinition def = AuditMetadata.COMPLIANCE.get(a.id());
                        return new Finding(a.id(), def.title(), def.severity(), 0, 0, def.effortMinutes());
                    })
                    .sorted(Comparator.comparing(Finding::severity)
                            .thenComparingInt(Finding::effortMinutes))
                    .toList();
        }

        private static List<Finding> prioritiseTime(List<RawAudit> audits) {
            return audits.stream()
                    .filter(audit -> Arrays.stream(AuditCheckMetadata.values())
                            .anyMatch(m -> m.getId().equals(audit.id())))
                    .map(audit -> {
                        Severity severity = severityFor(audit);
                        if (severity == null) return null;
                        return new Finding(audit.id(),
                                getFindingDescription(audit),
                                severity,
                                audit.lcpSavingsMs(),
                                audit.fcpSavingsMs(),
                                getFindingEffort(audit));
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(Finding::lcpSavingsMs).reversed())
                    .toList();
        }

        public static List<Finding> prioritise(List<RawAudit> audits) {
            List<Finding> result = new ArrayList<>(prioritiseTime(audits).stream().limit(4).toList());

            for (Finding f : prioritiseCompliance(audits)) {
                if (result.size() >= 5) break;
                result.add(f);
            }
            return result;
        }

//        public static List<Finding> prioritise(List<RawAudit> audits){
//
//            List<RawAudit> whiteAuditList = audits.stream()
//                    .filter(audit -> Arrays.stream(AuditCheckMetadata.values())
//                            .anyMatch(metadata -> metadata.getId().equals(audit.id())))
//                            .toList();
//
//
//            List<Finding> Auditresults = new ArrayList<>();
//
//           for(RawAudit audit : whiteAuditList){
//
//                if(audit.lcpSavingsMs()>250){
//                    if (audit.lcpSavingsMs() >= 2000 ){
//                        Auditresults.add(
//                                new Finding(audit.id(),
//                                        getFindingDescription(audit),
//                                        Severity.CRITICAL,
//                                        audit.lcpSavingsMs(),
//                                        audit.fcpSavingsMs(),
//                                        getFindingEffort(audit)
//                                ));
//                    }
//
//                    if(audit.lcpSavingsMs() <= 2000 && audit.lcpSavingsMs() >= 1000){
//                        Auditresults.add(
//                                new Finding(audit.id(),
//                                        getFindingDescription(audit),
//                                        Severity.HIGH,
//                                        audit.lcpSavingsMs(),
//                                        audit.fcpSavingsMs(),
//                                        getFindingEffort(audit)
//                                ));
//                    }
//
//                    if(audit.lcpSavingsMs() <= 1000 && audit.lcpSavingsMs() >= 500 ){
//                        Auditresults.add(
//                                new Finding(audit.id(),
//                                        getFindingDescription(audit),
//                                        Severity.MEDIUM,
//                                        audit.lcpSavingsMs(),
//                                        audit.fcpSavingsMs(),
//                                        getFindingEffort(audit)
//                                ));
//                    }
//
//                    if(audit.lcpSavingsMs() <= 250 ){
//                        Auditresults.add(
//                                new Finding(audit.id(),
//                                        getFindingDescription(audit),
//                                        Severity.LOW,
//                                        audit.lcpSavingsMs(),
//                                        audit.fcpSavingsMs(),
//                                        getFindingEffort(audit)
//                                )
//                        );
//                    }
//
//
//                }
//                else if (audit.fcpSavingsMs()>=250) {
//                        Auditresults.add(
//                                new Finding(audit.id(),
//                                        getFindingDescription(audit),
//                                        Severity.LOW,
//                                        audit.lcpSavingsMs(),
//                                        audit.fcpSavingsMs(),
//                                        getFindingEffort(audit)
//                                ));
//
//                }
//
//           }
//            Auditresults.sort(
//                    Comparator.comparingInt(Finding::lcpSavingsMs).reversed()
//            );
//
//           //Limit of 5 audits
//            if (Auditresults.size() > 5) {
//                Auditresults = new ArrayList<>(Auditresults.subList(0, 5));
//            }
//           return Auditresults;
//        }
//


}
