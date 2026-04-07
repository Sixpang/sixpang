package org.sixpang.commonserver.security;

import jakarta.servlet.http.HttpServletResponse;
import org.sixpang.commonserver.security.handler.CustomAccessDeniedHandler;
import org.sixpang.commonserver.security.handler.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final HeaderAuthenticationFilter headerAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(HeaderAuthenticationFilter headerAuthenticationFilter,
                          CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.headerAuthenticationFilter = headerAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                //CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // Security 예외 처리 추가
                // (코드리뷰:// Security 레벨에서 발생하는 인증/인가 예외는
                // Controller까지 도달하지 않기 때문에 GlobalExceptionHandler에서 처리되지 않음.
                // 따라서 임시로 SecurityConfig의 exceptionHandling에서 직접 응답을 처리하도록 설정.)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .addFilterBefore(headerAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}