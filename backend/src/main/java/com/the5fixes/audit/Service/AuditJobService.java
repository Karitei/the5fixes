package com.the5fixes.audit.Service;

import com.the5fixes.audit.AuditWorker;
import com.the5fixes.audit.Entity.AuditJob;
import com.the5fixes.audit.Entity.AuditResult;
import com.the5fixes.audit.Repository.AuditJobRepository;
import com.the5fixes.audit.Repository.AuditResultRepository;
import com.the5fixes.audit.Type.JobStatus;
import com.the5fixes.audit.UrlNormalizer;
import com.the5fixes.audit.dto.AuditScores;
import com.the5fixes.audit.dto.AuditStatusResponse;
import com.the5fixes.common.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
public class AuditJobService {
    private final AuditJobRepository repository;
    private final AuditWorker worker;
    private final AuditResultRepository resultRepository;


    public AuditJobService(AuditJobRepository repository, AuditWorker worker, AuditResultRepository resultRepository) {
        this.repository = repository;
        this.worker = worker;
        this.resultRepository = resultRepository;
    }


    public UUID AuditSubmit(String rawUrl){
        String normalizedUrl = UrlNormalizer.normalize(rawUrl);

        AuditJob job = new AuditJob();
        job.setUrl(rawUrl);
        job.setNormalizedUrl(normalizedUrl);
        job.setStatus(JobStatus.PENDING);
        AuditJob saved = repository.save(job);

        worker.process(saved.getId());
        return saved.getId();
    }


    public AuditStatusResponse getResult(UUID jobId){

        AuditJob job = repository.findById(jobId)
                .orElseThrow(()->new NotFoundException("Audit not found" + jobId));

        if(job.getStatus() == JobStatus.DONE){
            AuditResult result = resultRepository.findByJobId(jobId)
                    .orElseThrow(()->new NotFoundException("Audit not found" + jobId));

            AuditScores scores = new AuditScores(
                    result.getPerformanceScore(),
                    result.getAccessibilityScore(),
                    result.getBestPracticesScore(),
                    result.getSeoScore(),
                    result.getLoadTimeSeconds(),
                    result.getPageSizeBytes());

            return new AuditStatusResponse(
                    jobId,
                    job.getStatus(),
                    scores,
                    null
            );

        } else if (job.getStatus() == JobStatus.PENDING || job.getStatus() == JobStatus.RUNNING) {
            return new AuditStatusResponse(
                    jobId,
                    job.getStatus(),
                    null,
                    null
            );
        }else {
            return new AuditStatusResponse(
                    jobId,
                    job.getStatus(),
                    null,
                    job.getErrorMessage());
        }
    }
}
