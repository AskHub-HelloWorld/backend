package com.example.ask_hub.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // Global, Gxx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "예상치 못한 서버 오류입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "G002", "유효하지 않은 입력값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
