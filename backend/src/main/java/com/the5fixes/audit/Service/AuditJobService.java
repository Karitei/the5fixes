package com.the5fixes.audit.Service;

import com.the5fixes.audit.AuditWorker;
import com.the5fixes.audit.Entity.AuditFinding;
import com.the5fixes.audit.Entity.AuditJob;
import com.the5fixes.audit.Entity.AuditResult;
import com.the5fixes.audit.Repository.AuditFindingRepository;
import com.the5fixes.audit.Repository.AuditJobRepository;
import com.the5fixes.audit.Repository.AuditResultRepository;
import com.the5fixes.audit.Type.JobStatus;
import com.the5fixes.audit.UrlNormalizer;
import com.the5fixes.audit.dto.AuditScores;
import com.the5fixes.audit.dto.AuditStatusResponse;
import com.the5fixes.audit.dto.FindingResponse;
import com.the5fixes.common.NotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
public class AuditJobService {
    private final AuditJobRepository repository;
    private final AuditWorker worker;
    private final AuditResultRepository resultRepository;

    private final AuditFindingRepository findingRepository;


    public AuditJobService(AuditJobRepository repository, AuditWorker worker, AuditResultRepository resultRepository, AuditFindingRepository findingRepository) {
        this.repository = repository;
        this.worker = worker;
        this.resultRepository = resultRepository;
        this.findingRepository = findingRepository;
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

            //get AuditScores
            AuditResult result = resultRepository.findByJobId(jobId)
                    .orElseThrow(()->new NotFoundException("Audit not found" + jobId));

            AuditScores scores = new AuditScores(
                    result.getPerformanceScore(),
                    result.getAccessibilityScore(),
                    result.getBestPracticesScore(),
                    result.getSeoScore(),
                    result.getLoadTimeSeconds(),
                    result.getPageSizeBytes());

            //get AuditFindings
            List<AuditFinding> findings = findingRepository.findByJobIdOrderByDisplayOrder(jobId);

            List<FindingResponse> responses = findings.stream()
                    .map(f -> new FindingResponse(
                            f.getAuditId(),
                            f.getSeverity(),
                            f.getTitle(),
                            f.getLcpSavingsMs(),
                            f.getFcpSavingsMs(),
                            f.getEffortMinutes()))
                    .toList();

            return new AuditStatusResponse(
                    jobId,
                    job.getStatus(),
                    scores,
                    responses,
                    null
            );

        } else if (job.getStatus() == JobStatus.PENDING || job.getStatus() == JobStatus.RUNNING) {
            return new AuditStatusResponse(
                    jobId,
                    job.getStatus(),
                    null,
                    null,
                    null
            );
        }else {
            return new AuditStatusResponse(
                    jobId,
                    job.getStatus(),
                    null,
                    null,
                    job.getErrorMessage());
        }
    }
}
