package com.the5fixes.audit;

import com.the5fixes.audit.Entity.AuditFinding;
import com.the5fixes.audit.Entity.AuditJob;
import com.the5fixes.audit.Entity.AuditResult;
import com.the5fixes.audit.Repository.AuditJobRepository;
import com.the5fixes.audit.Repository.AuditResultRepository;
import com.the5fixes.audit.Repository.AuditFindingRepository;
import com.the5fixes.audit.Type.Finding;
import com.the5fixes.audit.Type.JobStatus;
import com.the5fixes.audit.Type.LighthouseRecord;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AuditWorker {
    private final AuditJobRepository repository;
    private final AuditResultRepository resultRepository;

    private final AuditFindingRepository findingRepository;
    private final LighthouseRunner runner;

    public AuditWorker(AuditJobRepository repository, AuditResultRepository resultRepository, AuditResultRepository findingRepository, AuditFindingRepository findingRepository1, LighthouseRunner runner) {
        this.repository = repository;
        this.resultRepository = resultRepository;
        this.findingRepository = findingRepository1;
        this.runner = runner;
    }

    @Async
    public void process(UUID jobId){
        AuditJob job = repository.findById(jobId).orElse(null);
        if(job == null){
            log.error("Job not found : {}", jobId);
            return;
        }

        try{

            job.setStatus(JobStatus.RUNNING);
            repository.save(job);

            LighthouseRecord record = runner.run(job.getNormalizedUrl());

            // Save AuditResult
            AuditResult result = new AuditResult(
                    record.getPerformance(),
                    record.getAccessibility(),
                    record.getBestPractices(),
                    record.getSeo(),
                    (Double) record.getLoadTimeSeconds(),
                    (long) record.getPageSizeBytes()

            );

            result.setJob(job);
            resultRepository.save(result);

            //save AuditFinding
            List<Finding> findings =  FindingEngine.Properties(record.getRawAudits());

            List<AuditFinding> entities = new ArrayList<>();

            for (int i = 0; i < findings.size(); i++){
                Finding f = findings.get(i);

                AuditFinding entity = new AuditFinding();
                entity.setJob(job);
                entity.setAuditId(f.id());
                entity.setSeverity(f.severity());
                entity.setTitle(f.title());
                entity.setLcpSavingsMs(f.lcpSavingsMs());
                entity.setFcpSavingsMs(f.fcpSavingsMs());
                entity.setEffortMinutes(f.effortMinutes());
                entity.setDisplayOrder(i + 1);

                entities.add(entity);
            }

            findingRepository.saveAll(entities);


            // Update job status
            job = repository.findById(jobId).orElse(null);

            job.setStatus(JobStatus.DONE);
            job.setCompletedAt(Instant.now());
            log.info("Audit done with {} (performance {})",
                    job.getNormalizedUrl(), result.getPerformanceScore());


        }catch (Exception e) {
            job = repository.findById(jobId).orElse(null);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            log.warn("Audit failed for {} : {}", job.getNormalizedUrl(), e.getMessage());

        } finally {

            job.setCompletedAt(Instant.now());
            repository.save(job);
        }
    }
}
