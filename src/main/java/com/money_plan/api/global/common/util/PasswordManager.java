package com.money_plan.api.global.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PasswordManager {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 비밀번호 암호화
     */
    public String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    /**
     * 비밀번호 검증
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
