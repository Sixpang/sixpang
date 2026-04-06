package org.sixpang.commonserver.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
/** Spring Security 설정 담당 **/
public class SecurityConfig {

    private final HeaderAuthenticationFilter headerAuthenticationFilter;

    public SecurityConfig(HeaderAuthenticationFilter headerAuthenticationFilter) {
        this.headerAuthenticationFilter = headerAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // Security 예외 처리 추가
                // (코드리뷰:// Security 레벨에서 발생하는 인증/인가 예외는
                // Controller까지 도달하지 않기 때문에 GlobalExceptionHandler에서 처리되지 않음.
                // 따라서 임시로 SecurityConfig의 exceptionHandling에서 직접 응답을 처리하도록 설정.)
                .exceptionHandling(exception -> exception
                        // 인증 실패 (401)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                        {
                          "code": "AUTH_401",
                          "message": "인증이 필요합니다."
                        }
                    """);
                        })

                        // 권한 부족 (403)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                        {
                          "code": "AUTH_403",
                          "message": "접근 권한이 없습니다."
                        }
                    """);
                        })
                )

                // 커스텀 필터 등록
                .addFilterBefore(headerAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}