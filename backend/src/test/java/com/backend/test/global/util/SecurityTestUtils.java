package com.backend.test.global.util;

import com.backend.global.auth.model.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class SecurityTestUtils {

    public static Authentication createAuthentication(CustomUserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
