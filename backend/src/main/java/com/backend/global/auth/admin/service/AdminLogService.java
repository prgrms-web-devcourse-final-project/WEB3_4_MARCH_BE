package com.backend.global.auth.admin.service;


import com.backend.global.auth.admin.entity.AdminLog;
import com.backend.global.auth.admin.repository.AdminLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    public AdminLogService(AdminLogRepository adminLogRepository) {
        this.adminLogRepository = adminLogRepository;
    }

    public void logAction(String actionType, String description) {
        AdminLog log = AdminLog.builder()
                .actionType(actionType)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();

        adminLogRepository.save(log);
    }
}