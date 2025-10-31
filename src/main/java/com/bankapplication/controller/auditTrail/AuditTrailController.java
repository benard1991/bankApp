package com.bankapplication.controller.auditTrail;

import com.bankapplication.dto.GenericResponse;
import com.bankapplication.model.AuditTrail;
import com.bankapplication.service.AuditTrailService.AuditTrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AuditTrailController {

    private final AuditTrailService auditTrailService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/audit-trails")
    public ResponseEntity<GenericResponse<List<AuditTrail>>> getAllAuditLogs() {
        List<AuditTrail> logs = auditTrailService.getAllAuditLogs();
        return ResponseEntity.ok(new GenericResponse<>(logs, "success", HttpStatus.OK.value()));
    }


}
