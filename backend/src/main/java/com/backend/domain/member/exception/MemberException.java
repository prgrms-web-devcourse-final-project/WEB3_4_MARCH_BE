package com.backend.domain.member.exception;

import org.springframework.http.HttpStatus;

/**
 * 회원(Member) 도메인에서 발생할 수 있는 모든 예외 상황을 처리하기 위해 사용
 * MemberService 내부에서 특정 조건이 충족되지 않을 때 MemberException을 던져서
 * 공통 예외 핸들러(GlobalExceptionHandler)가 처리
 */
public class MemberException extends RuntimeException {
    private final MemberErrorCode memberErrorCode;

    public MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.name());
        this.memberErrorCode = memberErrorCode;
    }

    public HttpStatus getStatus() {
        return memberErrorCode.getHttpStatus();
    }

    public String getCode() {
        return memberErrorCode.getCode();
    }
}
