package com.money_plan.api.global.security.filter;

import com.money_plan.api.domain.auth.type.TokenType;
import com.money_plan.api.global.common.exception.ErrorCode;
import com.money_plan.api.global.common.exception.JwtAuthenticationException;
import com.money_plan.api.global.common.util.TokenManager;
import com.money_plan.api.global.security.user.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.money_plan.api.global.security.config.SecurityConfig.PERMIT_URL_ARRAY;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenManager tokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 허용된 URL인 경우, 필터 체인을 그대로 진행
        if (isPermittedUrl(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        try {
            // Authorization 헤더 검증
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new JwtAuthenticationException(ErrorCode.AUTHORIZATION_HEADER_MISSING);
            }

            // Bearer 부분 제거 후 순수 토큰만 획득
            String accessToken = authorization.split(" ")[1];

            // 토큰 유효성 검증
            tokenManager.validateToken(accessToken);
            // 토큰 타입 확인
            if (!tokenManager.isTokenOf(accessToken, TokenType.ACCESS)){
                throw new JwtAuthenticationException(ErrorCode.TOKEN_TYPE_NOT_MATCHED);
            };

            Long userId = tokenManager.getUserId(accessToken);
            String account = tokenManager.getAccount(accessToken);

            //세션에 사용자 등록
            CustomUserDetails userDetails = new CustomUserDetails(userId, account);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException e) {
            handleException(request, response, e, filterChain);
        } catch (Exception e) {
            handleException(request, response, new JwtAuthenticationException(ErrorCode.INTERNAL_SERVER_ERROR, e), filterChain);
        }
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, JwtAuthenticationException e, FilterChain filterChain) throws IOException, ServletException {
        request.setAttribute("exception", e);
        filterChain.doFilter(request, response);
    }

    private boolean isPermittedUrl(String requestPath) {
        // PERMIT_URL_ARRAY에서 허용된 URL 경로를 확인
        for (String url : PERMIT_URL_ARRAY) {
            if (requestPath.startsWith(url.replace("/**", ""))) {
                return true;
            }
        }
        return false;
    }

}
