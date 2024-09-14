package com.money_plan.api.domain.auth.service;

import com.money_plan.api.domain.auth.dto.LoginRequestDto;
import com.money_plan.api.domain.auth.dto.SignUpRequestDto;
import com.money_plan.api.domain.auth.dto.TokenResponseDto;
import com.money_plan.api.domain.auth.type.TokenType;
import com.money_plan.api.domain.user.entity.User;
import com.money_plan.api.domain.user.repository.UserRepository;
import com.money_plan.api.global.common.exception.CustomException;
import com.money_plan.api.global.common.exception.ErrorCode;
import com.money_plan.api.global.common.util.PasswordManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordManager passwordManager;
    private final RedisTokenService tokenService;

    @Transactional
    public long signUp(SignUpRequestDto signUpRequestDto) {
        if (userRepository.existsByAccount(signUpRequestDto.getAccount())) {
            throw new CustomException(ErrorCode.ACCOUNT_ALREADY_REGISTERED);
        }

        return userRepository.save(
                User.builder()
                        .account(signUpRequestDto.getAccount())
                        .password(passwordManager.encodePassword(signUpRequestDto.getPassword()))
                        .createdAt(LocalDateTime.now())
                        .build()
        ).getId();
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        // 사용자 정보 확인
        User user = userRepository.findByAccount(loginRequestDto.getAccount()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordManager.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        // 기존 Refresh Token 삭제 & 신규 발급
        tokenService.deleteRefreshToken(user.getId());
        return tokenService.issueTokens(response, user.getAccount(), user.getId());
    }
}
