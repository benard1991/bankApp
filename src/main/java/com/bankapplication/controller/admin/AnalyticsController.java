package com.bankapplication.controller.admin;

import com.bankapplication.dto.GenericResponse;
import com.bankapplication.service.dashbordAnalytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/analytics")
    public ResponseEntity<?> getDashboardAnalytics() {
        Map<String, Object> stats = analyticsService.getDashboardData();
        return ResponseEntity.ok(new GenericResponse<>(stats, "success", HttpStatus.OK.value()));
    }

}
