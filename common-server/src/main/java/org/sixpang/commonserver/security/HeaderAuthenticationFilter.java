package org.sixpang.commonserver.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

/** Gateway에서 전달된 헤더 기반 인증 필터 */
@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Gateway에서 넣어준 사용자 정보 헤더 읽기
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        //디버깅: 헤더 값 확인
        System.out.println("[Service] X-User-Id = " + userId);
        System.out.println("[Service] X-User-Role = " + role);

        // 헤더 값이 있으면 인증 처리
        if (userId != null && role != null) {

            // 사용자 정보 객체 생성
            UserPrincipal principal =
                    new UserPrincipal(Long.parseLong(userId), role);

            // Spring Security 인증 객체 생성
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            //SecurityContext에 저장 (ThreadLocal에 저장됨)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}