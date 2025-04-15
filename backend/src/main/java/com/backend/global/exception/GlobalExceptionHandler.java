package com.backend.global.exception;

import com.backend.global.response.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * GlobalExceptionHandler
 * 전역 예외를 처리하기 위한 클래스
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 유효성 검사 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.info(" @Valid 유효성 검사 실패 시 발생하는 예외 처리",ex);

        List<String> errorMessages = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GenericResponse.fail(HttpStatus.BAD_REQUEST.value(), errorMessages.toString()));
    }

    /**
     * @RequestParam, @PathVariable 등의 유효성 검사 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GenericResponse<?>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> errorMessages = new ArrayList<>();
        ex.getConstraintViolations().forEach(v -> errorMessages.add(v.getPropertyPath() + ": " + v.getMessage()));
        log.info(" @PathVariable 등의 유효성 검사 실패 시 발생하는 예외 처리",ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GenericResponse.fail(HttpStatus.BAD_REQUEST.value(), errorMessages.toString()));
    }

    /**
     * Global 예외 처리
     */
//    @ExceptionHandler(GlobalException.class)
//    public ResponseEntity<GenericResponse<?>> handleGlobalException(GlobalException ex, HttpServletRequest request) {
//        return ResponseEntity
//                .status(ex.getStatus())
//                .body(GenericResponse.fail(ex.getStatus(), ex.getMessage()));
//    }
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GenericResponse<?>> handleGlobalException(GlobalException ex) {
        log.info("Global 예외 처리",ex);

        GlobalErrorCode errorCode = ex.getGlobalErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(GenericResponse.fail(errorCode.getHttpStatus().value(), errorCode.getMessage()));
    }

    /**
     * JWT 예외 처리
     */
//    @ExceptionHandler(JwtException.class)
//    public ResponseEntity<GenericResponse<?>> handleJwtException(JwtException ex) {
//        GlobalErrorCode errorCode = ex.getGlobalErrorCode();
//        return ResponseEntity
//                .status(errorCode.getHttpStatus())
//                .body(GenericResponse.fail(errorCode.getHttpStatus().value(), errorCode.getMessage()));
//    }

    /**
     * 예외가 명시되지 않은 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<?>> handleAllUnhandledException(Exception ex, HttpServletRequest request) {
        log.info("예외가 명시되지 않은 모든 예외 처리",ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(GenericResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 서버 오류가 발생했습니다."));
    }

}
