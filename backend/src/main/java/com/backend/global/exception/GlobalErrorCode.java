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

	INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
	SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류가 발생했습니다."),

	// 이미지 도메인 세부 에러
	IMAGE_COUNT_INVALID(HttpStatus.BAD_REQUEST, 400, "이미지는 1장 이상 5장 이하로 등록해야 합니다."),
	IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, 400, "이미지를 찾을 수 없습니다."),
	UNAUTHORIZED_IMAGE_OPERATION(HttpStatus.BAD_REQUEST, 400, "다른 유저의 이미지는 조작할 수 없습니다."),
	ALREADY_PRIMARY_IMAGE(HttpStatus.BAD_REQUEST, 400, "이미 대표 이미지로 설정되어 있습니다."),

	// 채팅요청 오류코드
	ALREADY_REQUESTED(HttpStatus.BAD_REQUEST, 400, "이미 채팅요청을 보냈습니다."),
	NOT_FOUND_CHAT_REQUEST(HttpStatus.NOT_FOUND, 404, "해당 채팅요청을 찾을 수 없습니다."),
	ALREADY_PROCESSED_CHAT_REQUEST(HttpStatus.BAD_REQUEST, 400, "이미 처리된 채팅 요청입니다."),


	// 멤버 오류코드
	NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, 404, "해당 유저를 찾을 수 없습니다."),
	DUPLICATE_MEMBER(HttpStatus.CONFLICT, 409, "이미 등록된 회원입니다.");


	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
