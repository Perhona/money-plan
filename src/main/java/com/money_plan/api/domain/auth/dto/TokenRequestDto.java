package com.money_plan.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
@Schema(name = "TokenRequestDto", description = "토큰 재발급 요청 DTO")
public class TokenRequestDto {

    @NotEmpty(message = "JWT 토큰 값을 입력해주세요.")
    @Schema(description = "유효한 JWT Refresh Token")
    private String refreshToken;
}
