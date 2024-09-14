package com.money_plan.api.global.security.jwt;

import com.money_plan.api.domain.auth.type.TokenType;
import com.money_plan.api.global.common.exception.ErrorCode;
import com.money_plan.api.global.common.exception.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token); // JWT 서명 및 유효성 검증
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException(ErrorCode.TOKEN_EXPIRED, e);
        } catch (MalformedJwtException | UnsupportedJwtException |
                 SignatureException | IllegalArgumentException e) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_TOKEN, e);
        }
    }

    public String getAccount(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("account", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
    }

    public String createJwt(TokenType tokenType, String username, Long userId) {
        return Jwts.builder()
                .claim("category", tokenType.getName())
                .claim("account", username)
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ((long) tokenType.getExpirationHour() * 60 * 60 * 1000)))
                .signWith(secretKey)
                .compact();
    }
}
