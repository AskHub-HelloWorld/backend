package com.example.ask_hub.global.domain;

import com.example.ask_hub.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonResponse<T> {

    enum Result {
        SUCCESS,
        FAILURE
    }

    private Result result;
    private String code;
    private String message;
    private T data;

    private CommonResponse(Result result, String code, String message, T data) {
        this.result = result;
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(Result.SUCCESS, "200", "요청이 성공했습니다.", data);
    }

    public static <T> CommonResponse<T> fail(ErrorCode errorCode) {
        return new CommonResponse<>(Result.FAILURE, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> CommonResponse<T> fail(ErrorCode errorCode, String message) {
        return new CommonResponse<>(Result.FAILURE, errorCode.getCode(), message, null);
    }
}