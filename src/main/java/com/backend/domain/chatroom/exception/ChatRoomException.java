package com.backend.domain.chatroom.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 채팅 도메인에서 발생할 수 있는 모든 예외 상황을 처리하기 위해 사용
 */
@Getter
public class ChatRoomException extends RuntimeException {

    private final ChatRoomErrorCode chatRoomErrorCode;

    public ChatRoomException(ChatRoomErrorCode chatRoomErrorCode) {
        super(chatRoomErrorCode.getMessage());
        this.chatRoomErrorCode = chatRoomErrorCode;
    }

    public HttpStatus getStatus() {
        return chatRoomErrorCode.getHttpStatus();
    }


}
