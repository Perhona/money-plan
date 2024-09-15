package com.money_plan.api.domain.auth.controller;

import com.money_plan.api.domain.auth.dto.LoginRequestDto;
import com.money_plan.api.domain.auth.dto.SignUpRequestDto;
import com.money_plan.api.domain.auth.dto.TokenRequestDto;
import com.money_plan.api.domain.auth.dto.TokenResponseDto;
import com.money_plan.api.domain.auth.service.AuthService;
import com.money_plan.api.global.common.exception.CustomException;
import com.money_plan.api.global.common.exception.JwtAuthenticationException;
import com.money_plan.api.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "사용자 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    /**
     * 사용자 회원가입
     *
     * @param signUpRequestDto 회원가입 요청 DTO
     * @return 사용자 Id
     * @throws CustomException 계정명 중복 -> ACCOUNT_ALREADY_REGISTERED
     */
    @Operation(summary = "사용자 회원가입", description = "사용자는 계정과 비밀번호로 회원 가입합니다.")
    @ApiResponse(
            responseCode = "200"
            , description = "회원가입 성공"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class))
    )
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Long>> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        CommonResponse<Long> response = CommonResponse.ok("회원가입에 성공했습니다.", authService.signUp(signUpRequestDto));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    /**
     * 사용자 로그인
     *
     * @param loginRequestDto 로그인 요청 DTO
     * @return tokenResponseDto
     * @throws CustomException 인증 실패 -> USER_NOT_FOUND, PASSWORD_NOT_MATCHED
     */
    @Operation(summary = "사용자 로그인", description = "사용자는 계정과 비밀번호로 로그인합니다.")
    @ApiResponse(
            responseCode = "200"
            , description = "로그인 성공"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDto.class))
    )
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<TokenResponseDto>> login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return new ResponseEntity<>(CommonResponse.ok("로그인에 성공하였습니다.", authService.login(loginRequestDto, response)), HttpStatus.OK);
    }

    /**
     * Access Token 재발급, Refresh Token 갱신
     *
     * @return tokenResponseDto
     * @throws JwtAuthenticationException RefreshToken이 유효하지 않은 경우
     */
    @Operation(summary = "Access Token 재발급, Refresh Token 갱신", description = "기존 Refresh Token으로 AccessToken과 RefreshToken을 재발급합니다. Access Token은 헤더에 저장됩니다.")
    @ApiResponse(
            responseCode = "200"
            , description = "Access Token 재발급, Refresh Token 갱신 완료"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDto.class))
    )
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<TokenResponseDto>> reissueAccessToken(@RequestBody @Valid TokenRequestDto tokenRequestDto, HttpServletResponse response) {
        return new ResponseEntity<>(CommonResponse.ok("Access Token 재발급, Refresh Token 갱신에 성공했습니다.", authService.reissueTokens(tokenRequestDto, response)), HttpStatus.OK);
    }
}
