package com.backend.global.auth.kakao.util;

import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GlobalException(GlobalErrorCode.AUTHENTICATION_REQUIRED);
        }

        // JwtFilter에서 넣어준 principal이 그냥 Long인 경우
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getMemberId();
        }

        throw new GlobalException(GlobalErrorCode.AUTHENTICATION_REQUIRED);
    }
}
