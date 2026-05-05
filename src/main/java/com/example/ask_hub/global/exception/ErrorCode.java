package com.example.ask_hub.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // Global, Gxx1
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "예상치 못한 서버 오류입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "G002", "유효하지 않은 입력값입니다."),

    // Auth, Axx1
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "A001", "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "A002", "잘못된 비밀번호입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "A003", "유효하지 않은 토큰 정보입니다."),

    // User, Uxx1,
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "회원을 찾을 수 없습니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}
