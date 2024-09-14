package com.money_plan.api.domain.auth.service;

public interface TokenService {
    /**
     * RefreshToken 저장
     */
    void saveRefreshToken(Long userId, String refreshToken, long durationInHours);

    /**
     * RefreshToken 조회
     */
    String getRefreshToken(Long userId);

    /**
     * RefreshToken 삭제
     */
    void deleteRefreshToken(Long userId);
}
