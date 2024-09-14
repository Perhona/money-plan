package com.money_plan.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(description = "회원가입 요청 DTO")
@Getter
public class SignUpRequestDto {

    @Schema(description = "사용자 계정", example = "john_doe")
    @NotBlank(message = "계정은 필수 항목입니다.")
    @Size(min = 4, max = 20, message = "사용자 계정명은 4자에서 20자 사이여야 합니다.")
    private String account;

    @Schema(description = "사용자 비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
}
