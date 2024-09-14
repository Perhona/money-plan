package com.money_plan.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "토큰 응답 DTO")
public class TokenResponseDto {

    @Schema(description = "JWT Access Token")
    private String accessToken;

    @Schema(description = "JWT Refresh Token")
    private String refreshToken;
}
