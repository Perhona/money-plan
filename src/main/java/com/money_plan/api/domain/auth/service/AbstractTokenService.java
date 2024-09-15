package com.money_plan.api.domain.auth.service;

import com.money_plan.api.domain.auth.dto.TokenResponseDto;
import com.money_plan.api.domain.auth.type.TokenType;
import com.money_plan.api.global.common.exception.ErrorCode;
import com.money_plan.api.global.common.exception.JwtAuthenticationException;
import com.money_plan.api.global.common.util.TokenManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public abstract class AbstractTokenService {
    private final TokenManager tokenManager;

    /**
     * RefreshToken 저장
     */
    protected abstract void saveRefreshToken(Long userId, String refreshToken, long durationInHours);

    /**
     * RefreshToken 조회
     */
    protected abstract String getRefreshToken(Long userId);

    /**
     * RefreshToken 삭제
     */
    protected abstract void deleteRefreshToken(Long userId);


    @Transactional
    public TokenResponseDto issueTokens(HttpServletResponse response, String username, Long userId) {
        // JWT 생성
        String accessToken = tokenManager.createToken(TokenType.ACCESS, username, userId);
        String refreshToken = tokenManager.createToken(TokenType.REFRESH, username, userId);

        // Redis에 Refresh Token 저장
        saveRefreshToken(userId, refreshToken, TokenType.REFRESH.getExpirationHour());

        // Header 에 AT 추가
        response.addHeader("Authorization", "Bearer " + accessToken);

        // RT 응답
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void validateRefreshToken(String refreshToken) {
        // 1. 토큰 자체 유효성 검사
        tokenManager.validateToken(refreshToken);

        // 2. 토큰 종류 확인
        if (!tokenManager.isTokenOf(refreshToken, TokenType.REFRESH)) {
            throw new JwtAuthenticationException(ErrorCode.TOKEN_TYPE_NOT_MATCHED);
        }

        // 3. DB RefreshToken
        String storedRefreshToken = getRefreshToken(tokenManager.getUserId(refreshToken));
        if (refreshToken == null || !refreshToken.equals(storedRefreshToken)) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    @Transactional
    public TokenResponseDto reissueTokens(HttpServletResponse response, String refreshToken) {
        String username = tokenManager.getUsername(refreshToken);
        Long userId = tokenManager.getUserId(refreshToken);
        deleteRefreshToken(userId);
        return issueTokens(response, username, userId);
    }
}
