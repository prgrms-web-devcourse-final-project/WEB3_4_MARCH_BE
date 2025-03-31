package com.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),

    NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, 1000, "사용자를 찾을 수 없습니다."),
    ALREADY_EXISTS_CHATROOM(HttpStatus.CONFLICT, 1002, "이미 채팅방이 존재합니다."),
    BLOCKED_MEMBER(HttpStatus.FORBIDDEN, 1003, "상대방에게 차단되었습니다."),

    // TODO : ChatRequest 머지 후 삭제 예정
    NOT_FOUND_BY_REQUEST(HttpStatus.NOT_FOUND, 1004, "요청이 존재하지 않습니다."),
    DUPLICATE_CHAT_REQUEST(HttpStatus.CONFLICT, 1005, "이미 채팅 요청이 존재합니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
