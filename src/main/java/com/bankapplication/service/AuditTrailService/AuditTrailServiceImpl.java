package com.bankapplication.service.AuditTrailService;

import com.bankapplication.model.AuditTrail;
import com.bankapplication.repository.AuditTrailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditTrailServiceImpl implements AuditTrailService {

    private final AuditTrailRepository auditTrailRepository;

    @Override
    public void recordAudit(Long userId, String username, String action, String entityType, String entityId, String oldValue, String newValue, String actionDescription, String ipAddress, String status) {
        AuditTrail trail = AuditTrail.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .actionDescription(actionDescription)
                .ipAddress(ipAddress)
                .status(status)
                .performedAt(LocalDateTime.now())
                .build();

        auditTrailRepository.save(trail);
    }

    @Override
    public List<AuditTrail> getAllAuditLogs() {
        return auditTrailRepository.findAll(Sort.by(Sort.Direction.DESC, "performedAt"));
    }

}
