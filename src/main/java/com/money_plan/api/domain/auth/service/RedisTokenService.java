package com.money_plan.api.domain.auth.service;

import com.money_plan.api.domain.auth.dto.TokenResponseDto;
import com.money_plan.api.domain.auth.type.TokenType;
import com.money_plan.api.domain.user.entity.User;
import com.money_plan.api.global.common.exception.ErrorCode;
import com.money_plan.api.global.common.exception.JwtAuthenticationException;
import com.money_plan.api.global.common.util.TokenManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenService implements TokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenManager tokenManager;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token";


    @Override
    public void saveRefreshToken(Long userId, String refreshToken, long durationInHours) {
        String key = generateKey(userId);
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofHours(durationInHours));
    }

    @Override
    public String getRefreshToken(Long userId) {
        String key = generateKey(userId);
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteRefreshToken(Long userId) {
        String key = generateKey(userId);
        redisTemplate.delete(key);
    }

    /**
     * Key 생성 메소드
     */
    private String generateKey(Long userId) {
        return String.format("%s:%s", REFRESH_TOKEN_PREFIX, userId);
    }

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

}
