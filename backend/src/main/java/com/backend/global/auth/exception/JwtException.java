package com.backend.global.auth.exception;

import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;

/**
 * JWT 관련 예외 클래스
 * GlobalException을 상속받아 GlobalErrorCode 기반으로 예외 처리한다.
 */

public class JwtException extends GlobalException {
    public JwtException(GlobalErrorCode errorCode) {
        super(errorCode);
    }
}