package com.money_plan.api.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.money_plan.api.global.common.exception.ErrorCode;
import com.money_plan.api.global.common.exception.JwtAuthenticationException;
import com.money_plan.api.global.common.response.ErrorResponse;
import com.money_plan.api.global.security.filter.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String[] PERMIT_URL_ARRAY = {
            "/v3/api-docs/**", "/swagger-ui/**", "/v3/api-docs", "/swagger-ui.html",
            "/error", "/signup", "/login", "/reissue"
    };

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMIT_URL_ARRAY).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));
        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final AuthenticationEntryPoint authenticationEntryPoint =
            (request, response, authException) -> {
                JwtAuthenticationException jwtException = (JwtAuthenticationException) request.getAttribute("exception");
                ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
                String errorMessage = authException.getMessage(); // 기본적으로 authException의 메시지

                if (jwtException != null) {
                    errorCode = jwtException.getErrorCode();
                    errorMessage = jwtException.getMessage();
                }

                log.info("Authentication Failed: {}", errorMessage);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, errorCode, errorMessage);
            };

    private final AccessDeniedHandler accessDeniedHandler =
            (request, response, accessDeniedException) -> {
                log.error("Access Denied: {}", accessDeniedException.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, ErrorCode.ACCESS_DENIED);
            };

    private static void sendErrorResponse(HttpServletResponse response, int status, ErrorCode errorCode) throws IOException {
        sendErrorResponse(response, status, errorCode, errorCode.getMessage());
    }

    private static void sendErrorResponse(HttpServletResponse response, int status, ErrorCode errorCode, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(new ObjectMapper().writeValueAsString(new ErrorResponse(errorCode, message)));
    }
}
