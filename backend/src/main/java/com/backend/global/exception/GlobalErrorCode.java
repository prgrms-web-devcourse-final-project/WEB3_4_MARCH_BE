package com.backend.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * GlobalErrorCode
 * 전역에서 발생 할 수 있는 커스텀 예외 정리 클래스
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {

	// 카카오 로그인 에러
	KAKAO_LOGIN_FAILED(HttpStatus.BAD_REQUEST, 400, "카카오 로그인 중 에러가 발생했습니다."),


	// 이미지 도메인 세부 에러
	IMAGE_COUNT_INVALID(HttpStatus.BAD_REQUEST, 400, "이미지는 1장 이상 5장 이하로 등록해야 합니다."),
	IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, 400, "이미지를 찾을 수 없습니다."),
	UNAUTHORIZED_IMAGE_OPERATION(HttpStatus.BAD_REQUEST, 400, "다른 유저의 이미지는 조작할 수 없습니다."),
	ALREADY_PRIMARY_IMAGE(HttpStatus.BAD_REQUEST, 400, "이미 대표 이미지로 설정되어 있습니다."),

	// 채팅요청 오류코드
	ALREADY_REQUESTED(HttpStatus.BAD_REQUEST, 400, "이미 채팅요청을 보냈습니다."),
	NOT_FOUND_CHAT_REQUEST(HttpStatus.NOT_FOUND, 404, "해당 채팅요청을 찾을 수 없습니다."),
	ALREADY_PROCESSED_CHAT_REQUEST(HttpStatus.BAD_REQUEST, 400, "이미 처리된 채팅 요청입니다."),
	DUPLICATE_CHAT_REQUEST(HttpStatus.BAD_REQUEST, 400, "이미 처리된 채팅 요청입니다."),
	SAME_SENDER_AND_RECEIVER(HttpStatus.BAD_REQUEST, 400, "요청자와 수신자는 같을 수 없습니다."),

	// 채팅 오류코드
	CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 채팅방을 찾을 수 없습니다."),
	CHATROOM_FORBIDDEN(HttpStatus.FORBIDDEN, 403, "채팅방에 접근할 수 없습니다."),

	// 멤버 오류코드
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 유저를 찾을 수 없습니다."),
	DUPLICATE_MEMBER(HttpStatus.CONFLICT, 409, "이미 등록된 회원입니다."),

	// 정리 필요
	SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류가 발생했습니다."),
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰입니다."),
	TOKEN_REISSUE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 401, "토큰 갱신에 실패했습니다."),
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 401, "토큰이 만료되었습니다."),
	UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, 401, "지원하지 않는 JWT 입니다."),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, 404, "리프레시 토큰이 존재하지 않습니다."),
	MEMBER_REGISTRATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 500, "멤버를 찾을 수 없습니다."),
	AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, 401, "인증된 사용자 정보를 가져올 수 없습니다."),
	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, 401, "Authorization 헤더가 존재하지 않거나 형식이 올바르지 않습니다."),
    KAFKA_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Kafka 메시지 전송 실패."),

    // 차단회원 오류코드
    BLOCKER_NOT_FOUND(HttpStatus.NOT_FOUND, 404-1, "차단자 유저가 존재하지 않습니다."),
    BLOCKED_USER_NOT_FOUND(HttpStatus.NOT_FOUND, 404-2, "차단 대상 유저가 존재하지 않습니다."),
    INVALID_BLOCK_SELF(HttpStatus.BAD_REQUEST, 400-1, "자기 자신을 차단할 수 없습니다."),
    USER_ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, 400-2, "이미 차단한 유저입니다."),
    ALREADY_UNBLOCK(HttpStatus.BAD_REQUEST, 400-3, "이미 차단해제된 유저입니다."),

    // 권한 없는 유저 오류코드
    TEMP_USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, 403, "추가 정보 입력이 필요합니다."),

	// 알림 오류코드
	NOTIFICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, 403, "자신의 알림만 처리할 수 있습니다.");



    private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
