package com.the5fixes.audit;

import com.the5fixes.audit.Entity.AuditJob;
import com.the5fixes.audit.Entity.AuditResult;
import com.the5fixes.audit.Repository.AuditJobRepository;
import com.the5fixes.audit.Repository.AuditResultRepository;
import com.the5fixes.audit.Type.JobStatus;
import com.the5fixes.audit.Type.LighthouseRecord;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class AuditWorker {
    private final AuditJobRepository repository;
    private final AuditResultRepository resultRepository;
    private final LighthouseRunner runner;

    public AuditWorker(AuditJobRepository repository, AuditResultRepository resultRepository, LighthouseRunner runner) {
        this.repository = repository;
        this.resultRepository = resultRepository;
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
