package com.money_plan.api.domain.auth.service;

import com.money_plan.api.global.common.util.TokenManager;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Primary
public class RedisTokenService extends AbstractTokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token";

    public RedisTokenService(TokenManager tokenManager, RedisTemplate<String, Object> redisTemplate) {
        super(tokenManager);
        this.redisTemplate = redisTemplate;
    }


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
}
