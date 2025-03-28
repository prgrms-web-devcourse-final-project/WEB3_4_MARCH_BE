package com.backend.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum MemberErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "존재하지 않는 회원입니다."),
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "409", "이미 등록된 회원입니다.");

//    INVALID_MODIFICATION_REQUEST(HttpStatus.BAD_REQUEST, "400","회원 정보 수정 요청이 잘못되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;


}
