package org.sixpang.commonserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
/**Spring Security 설정 담당 (필터를 실행시키는 설정) **/
public class SecurityConfig {

    //  만든 인증 필터 (헤더 기반)
    private final HeaderAuthenticationFilter headerAuthenticationFilter;

    public SecurityConfig(HeaderAuthenticationFilter headerAuthenticationFilter) {
        this.headerAuthenticationFilter = headerAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 만든 필터를 Spring Security 필터 체인에 등록
                // UsernamePasswordAuthenticationFilter 전에 실행되도록 설정
                .addFilterBefore(headerAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        // Security 필터 체인 생성
        return http.build();
    }
}