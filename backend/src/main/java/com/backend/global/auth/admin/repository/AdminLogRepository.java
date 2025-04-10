package com.backend.global.auth.admin.repository;

import com.backend.global.auth.admin.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AdminLog 엔티티의 기본 CRUD를 제공하는 Repository
 */

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
}