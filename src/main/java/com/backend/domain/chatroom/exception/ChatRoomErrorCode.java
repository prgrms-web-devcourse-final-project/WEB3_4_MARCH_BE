package com.backend.domain.chatroom.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatRoomErrorCode {

    CHAT_ROOM_BLOCKED(HttpStatus.FORBIDDEN, 403, "상대방에게 차단되었습니다."),             // 403
    ALREADY_EXISTS_CHATROOM(HttpStatus.CONFLICT, 409, "이미 채팅방이 존재합니다.");         // 409

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
