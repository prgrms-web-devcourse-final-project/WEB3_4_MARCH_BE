package com.backend.domain.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),

    NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, 404, "사용자를 찾을 수 없습니다."),               // 404

    // TODO : ChatRequest 머지 전 삭제 예정
    NOT_FOUND_BY_REQUEST(HttpStatus.NOT_FOUND, 40402, "요청이 존재하지 않습니다."),          // 404
    DUPLICATE_CHAT_REQUEST(HttpStatus.CONFLICT, 40902, "이미 채팅 요청이 존재합니다."),       // 409

    BLOCKED_MEMBER(HttpStatus.FORBIDDEN, 40301, "상대방에게 차단되었습니다.");             // 403

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
