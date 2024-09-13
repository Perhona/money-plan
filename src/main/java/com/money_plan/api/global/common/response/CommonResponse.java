package com.money_plan.api.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(description = "공통 응답 객체")
public class CommonResponse<T> {
    @Schema(description = "HTTP 상태 코드", example = "OK")
    private final HttpStatus httpStatus;
    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private final String message;
    @Schema(description = "응답 데이터")
    private final T data;

    private CommonResponse(HttpStatus status, String message, T data) {
        this.httpStatus = status;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> ok(T data) {
        return ok(null, data);
    }

    public static <T> CommonResponse<T> ok(String message) {
        return ok(message, null);
    }

    public static <T> CommonResponse<T> ok(String message, T data) {
        return new CommonResponse<>(HttpStatus.OK, message, data);
    }

    public static CommonResponse<Void> fail(String message) {
        return fail(message, null);
    }

    public static <T> CommonResponse<T> fail(String message, T data) {
        return new CommonResponse<>(HttpStatus.UNAUTHORIZED, message, data);
    }

}


