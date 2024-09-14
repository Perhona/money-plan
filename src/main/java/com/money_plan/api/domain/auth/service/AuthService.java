package com.money_plan.api.domain.auth.service;

import com.money_plan.api.domain.auth.dto.LoginRequestDto;
import com.money_plan.api.domain.auth.dto.SignUpRequestDto;
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
}
