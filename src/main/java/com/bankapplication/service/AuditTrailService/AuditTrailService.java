package com.bankapplication.service.AuditTrailService;

import com.bankapplication.model.AuditTrail;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuditTrailService {

    void recordAudit(Long userId,
                     String username,
                     String action,
                     String entityType,
                     String entityId,
                     String oldValue,
                     String newValue,
                     String actionDescription,
                     String ipAddress,
                     String status);


    List<AuditTrail> getAllAuditLogs();
}
