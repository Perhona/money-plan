package com.money_plan.api.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    // 공통
    INVALID_INPUT_VALUE(BAD_REQUEST, "유효하지 않은 입력 값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 오류가 발생했습니다."),
    ENDPOINT_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 엔드포인트를 찾을 수 없습니다."),

    // 인증&인가
    PASSWORD_NOT_MATCHED(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    AUTHENTICATION_FAILED(UNAUTHORIZED, "사용자 인증에 실패했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    AUTHORIZATION_HEADER_MISSING(UNAUTHORIZED, "Authorization 헤더값이 유효하지 않습니다."),
    TOKEN_EXPIRED(UNAUTHORIZED, "토큰 인증 시간이 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "토큰 형식이 유효하지 않습니다."),
    TOKEN_TYPE_NOT_MATCHED(UNAUTHORIZED, "토큰 타입이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "refresh 토큰이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요."),
    REFRESH_TOKEN_NOT_FOUND(UNAUTHORIZED, "refresh 토큰을 찾을 수 없습니다."),

    //  사용자,
    ACCOUNT_ALREADY_REGISTERED(CONFLICT, "중복된 게정이 존재합니다."),
    USER_NOT_FOUND(BAD_REQUEST, "사용자를 찾을 수 없습니다."),

    // 카테고리
    CATEGORY_NOT_FOUND(BAD_REQUEST, "카테고리를 찾을 수 없습니다."),
    CATEGORY_DUPLICATED(BAD_REQUEST, "중복된 카테고리가 존재합니다."),
    ;


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
