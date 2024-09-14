package com.money_plan.api.global.common.util;

import com.money_plan.api.domain.auth.type.TokenType;
import com.money_plan.api.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenManager {

    private final JwtTokenProvider jwtTokenProvider;

    public String createToken(TokenType tokenType, String username, Long userId){
        return jwtTokenProvider.createJwt(tokenType, username, userId);
    }

}
