package com.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * GlobalException
 * <p>공통으로 사용할 예외 클래스 입니다. <br><br>
 * 사용 예시: </p>
 * {@code
 * throw new GlobalException(GlobalErrorCode.NOT_VALID);
 * }
 */
@Getter
public class GlobalException extends RuntimeException {

    private final GlobalErrorCode globalErrorCode;

    public GlobalException(GlobalErrorCode globalErrorCode) {
        super(globalErrorCode.getMessage());
        this.globalErrorCode = globalErrorCode;
    }

    public HttpStatus getStatus() {
        return globalErrorCode.getHttpStatus();
    }


}
