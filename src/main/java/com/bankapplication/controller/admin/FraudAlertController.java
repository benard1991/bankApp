package com.bankapplication.controller.admin;

import com.bankapplication.dto.CreateFraudAlertRequestDto;
import com.bankapplication.dto.CustomPageResponse;
import com.bankapplication.dto.FraudAlertResponse;
import com.bankapplication.dto.GenericResponse;
import com.bankapplication.model.BlacklistedIp;
import com.bankapplication.model.FraudAlert;
import com.bankapplication.service.FraudAlertService.FraudAlertService;
import com.bankapplication.service.FraudAlertService.FraudDetectionService;
import com.bankapplication.util.PaginationInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/fraud-alerts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class FraudAlertController {

    private final FraudAlertService fraudAlertService;
    private  final FraudDetectionService fraudDetectionService;

    @PostMapping("/create")
    public ResponseEntity<FraudAlertResponse> createAlert(
            @Valid @RequestBody CreateFraudAlertRequestDto request) {
        FraudAlertResponse response = fraudAlertService.createAlert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FraudAlertResponse>> getPendingAlerts() {
        return ResponseEntity.ok(fraudAlertService.getPendingAlerts());
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<FraudAlertResponse> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(fraudAlertService.resolveAlert(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FraudAlertResponse>> getAlertsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(fraudAlertService.getAlertsByUser(userId));
    }

    @GetMapping("/type/{transactionType}")
    public ResponseEntity<List<FraudAlertResponse>> getAlertsByType(@PathVariable String transactionType) {
        return ResponseEntity.ok(fraudAlertService.getAlertsByTransactionType(transactionType));
    }

    @GetMapping("/blacklisted-ips")
    public ResponseEntity<GenericResponse<?>> getBlacklistedIps(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<BlacklistedIp> ipPage = fraudDetectionService.getAllBlacklistedIps(pageable);

        PaginationInfo paginationInfo = new PaginationInfo(
                ipPage.getSize(),
                ipPage.getNumber(),
                ipPage.getTotalPages(),
                ipPage.getTotalElements()
        );

        CustomPageResponse response = new CustomPageResponse(ipPage.getContent(), paginationInfo);

        return ResponseEntity.ok(
                new GenericResponse<>(response, "Blacklisted IPs retrieved successfully", 200)
        );
    }
}
