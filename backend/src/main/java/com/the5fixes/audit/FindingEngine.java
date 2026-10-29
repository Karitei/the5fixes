package com.the5fixes.audit;

import com.the5fixes.audit.Type.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;



@Component
public class FindingEngine {
        private final List<RawAudit> audits;

        public FindingEngine(List<RawAudit> audits) {
            this.audits = audits;
        }

        public static List<Finding> Properties(List<RawAudit> audits){

            List<RawAudit> whiteAuditList = audits.stream()
                    .filter(audit -> Arrays.stream(AuditCheckMetadata.values())
                            .anyMatch(metadata -> metadata.getId().equals(audit.id())))
                            .toList();


            List<Finding> Auditresults = new ArrayList<>();

           for(RawAudit audit : whiteAuditList){

                if(audit.lcpSavingsMs()>250){
                    if (audit.lcpSavingsMs() >= 2000 ){
                        Auditresults.add(
                                new Finding(audit.id(),
                                        Arrays.stream(AuditCheckMetadata.values())
                                                .filter(metadata -> metadata.getId().equals(audit.id()))
                                                .map(AuditCheckMetadata::getDescription)
                                                .findFirst()
                                                .orElse(""),
                                        Severity.CRITICAL,
                                        audit.lcpSavingsMs(),
                                        audit.fcpSavingsMs()
                                ));
                    }

                    if(audit.lcpSavingsMs() <= 2000 && audit.lcpSavingsMs() >= 1000){
                        Auditresults.add(
                                new Finding(audit.id(),
                                        Arrays.stream(AuditCheckMetadata.values())
                                                .filter(metadata -> metadata.getId().equals(audit.id()))
                                                .map(AuditCheckMetadata::getDescription)
                                                .findFirst()
                                                .orElse(""),
                                        Severity.HIGH,
                                        audit.lcpSavingsMs(),
                                        audit.fcpSavingsMs()
                                ));
                    }

                    if(audit.lcpSavingsMs() <= 1000 && audit.lcpSavingsMs() >= 500 ){
                        Auditresults.add(
                                new Finding(audit.id(),
                                        Arrays.stream(AuditCheckMetadata.values())
                                                .filter(metadata -> metadata.getId().equals(audit.id()))
                                                .map(AuditCheckMetadata::getDescription)
                                                .findFirst()
                                                .orElse(""),
                                        Severity.MEDIUM,
                                        audit.lcpSavingsMs(),
                                        audit.fcpSavingsMs()
                                ));
                    }

                    if(audit.lcpSavingsMs() <= 250 ){
                        Auditresults.add(
                                new Finding(audit.id(),
                                        Arrays.stream(AuditCheckMetadata.values())
                                                .filter(metadata -> metadata.getId().equals(audit.id()))
                                                .map(AuditCheckMetadata::getDescription)
                                                .findFirst()
                                                .orElse(""),
                                        Severity.LOW,
                                        audit.lcpSavingsMs(),
                                        audit.fcpSavingsMs()
                                ));
                    }


                }
                else if (audit.fcpSavingsMs()>=250) {
                        Auditresults.add(
                                new Finding(audit.id(),
                                        Arrays.stream(AuditCheckMetadata.values())
                                                .filter(metadata -> metadata.getId().equals(audit.id()))
                                                .map(AuditCheckMetadata::getDescription)
                                                .findFirst()
                                                .orElse(""),
                                        Severity.LOW,
                                        audit.lcpSavingsMs(),
                                        audit.fcpSavingsMs()
                                ));

                }

           }
            Auditresults.sort(
                    Comparator.comparingInt(Finding::lcpSavingsMs).reversed()
            );

            if (Auditresults.size() > 5) {
                Auditresults = new ArrayList<>(Auditresults.subList(0, 5));
            }
           return Auditresults;
        }



}
