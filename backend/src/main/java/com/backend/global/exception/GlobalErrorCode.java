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

    // 채팅요청 오류코드
    ALREADY_REQUESTED(HttpStatus.BAD_REQUEST, 400, "이미 채팅요청을 보냈습니다."),
    NOT_FOUND_CHAT_REQUEST(HttpStatus.NOT_FOUND, 404, "해당 채팅요청을 찾을 수 없습니다."),
    ALREADY_PROCESSED_CHAT_REQUEST(HttpStatus.BAD_REQUEST, 400, "이미 처리된 채팅 요청입니다."),


    // 멤버 오류코드
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, 404, "해당 유저를 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
