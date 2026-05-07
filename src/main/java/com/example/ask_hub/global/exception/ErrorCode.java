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
    SIGN_OUT_USER(HttpStatus.NOT_FOUND, "A004", "탈퇴한 회원입니다. 회원 복구를 진행하거나 재가입하십시오."),

    // User, Uxx1,
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "회원을 찾을 수 없습니다."),

    // Post, Pxx1
    DUPLICATED_TITLE(HttpStatus.BAD_REQUEST, "P001", "이미 존재하는 게시글 제목입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "게시글을 찾을 수 없습니다."),
    NOT_ALLOWED_USER(HttpStatus.METHOD_NOT_ALLOWED, "P003", "접근이 허용되지 않은 사용자입니다."),

    // Comment, Cxx1
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "답글을 찾을 수 없습니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}
