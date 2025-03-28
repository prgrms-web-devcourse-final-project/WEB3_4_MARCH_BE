package com.backend.domain.chat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 채팅 도메인에서 발생할 수 있는 모든 예외 상황을 처리하기 위해 사용
 */
@Getter
public class ChatException extends RuntimeException {

    private final ChatErrorCode chatErrorCode;

    public ChatException(ChatErrorCode chatErrorCode) {
        super(chatErrorCode.getMessage());
        this.chatErrorCode = chatErrorCode;
    }

    public HttpStatus getStatus() {
        return chatErrorCode.getHttpStatus();
    }


}
