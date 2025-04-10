package com.backend.global.auth.admin.controller;

import com.backend.global.auth.admin.dto.AdminLoginRequestDto;
import com.backend.global.auth.admin.dto.AdminLoginResponseDto;
import com.backend.global.auth.admin.service.AdminAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 전용 로그인 컨트롤러 클래스
 */

@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    // 관리자 전용 로그인 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDto> adminLogin(@RequestBody AdminLoginRequestDto request) {
        AdminLoginResponseDto responseDto = adminAuthService.processAdminLogin(request);
        return ResponseEntity.ok(responseDto);
    }
}