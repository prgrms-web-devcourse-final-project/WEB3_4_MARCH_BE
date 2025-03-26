package com.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * GlobalErrorCode
 * 전역에서 발생 할 수 있는 커스텀 예외 정리 클래스
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),

    CHAT_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, 1001, "사용자를 찾을 수 없습니다."),
    DUPLICATE_CHAT_REQUEST(HttpStatus.CONFLICT, 1002, "이미 채팅 요청이 존재합니다.");
    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
