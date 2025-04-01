package com.backend.global.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.backend.domain.member.exception.MemberException;
import com.backend.global.response.GenericResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * GlobalExceptionHandler
 * 전역 예외를 처리하기 위한 클래스
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 유효성 검사 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
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

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GenericResponse.fail(HttpStatus.BAD_REQUEST.value(), errorMessages.toString()));
    }

    /**
     * Member 도메인 예외 처리
     */
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<GenericResponse<?>> handleMemberException(MemberException ex, HttpServletRequest request) {
        int statusCode = ex.getHttpStatus().value();
        String fullMessage = String.format("[%s] %s (path: %s)", ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(ex.getHttpStatus())
                .body(GenericResponse.fail(statusCode, fullMessage));
    }

    /**
     * Global 예외 처리
     */
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GenericResponse<?>> handleGlobalException(GlobalException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(GenericResponse.fail(ex.getStatus(), ex.getMessage()));
    }

    /**
     * 예외가 명시되지 않은 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<?>> handleAllUnhandledException(Exception ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(GenericResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 서버 오류가 발생했습니다."));
    }

}
