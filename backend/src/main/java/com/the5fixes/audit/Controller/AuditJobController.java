package com.the5fixes.audit.Controller;

import com.the5fixes.audit.Entity.AuditJob;
import com.the5fixes.audit.Entity.AuditResult;
import com.the5fixes.audit.Service.AuditJobService;
import com.the5fixes.audit.Type.JobStatus;
import com.the5fixes.audit.dto.AuditRequest;
import com.the5fixes.audit.dto.AuditResponse;
import com.the5fixes.audit.dto.AuditScores;
import com.the5fixes.audit.dto.AuditStatusResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/audit")
public class AuditJobController {

    private final AuditJobService jobService;

    public AuditJobController(AuditJobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/submitAudit")
    public ResponseEntity<AuditResponse> submit(@Valid @RequestBody AuditRequest request){
        UUID jobId = jobService.AuditSubmit(request.url());
        return ResponseEntity
                .accepted()
                .body(new AuditResponse(jobId, JobStatus.PENDING));
    }

    @GetMapping("/{id}")
    public AuditStatusResponse status(@PathVariable UUID id){
        return jobService.getResult(id);
    }

}
