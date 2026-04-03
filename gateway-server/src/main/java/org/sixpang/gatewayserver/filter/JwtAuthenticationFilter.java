package org.sixpang.gatewayserver.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sixpang.gatewayserver.jwt.JwtProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**JWT 인증 필터 */
// 모든 요청에 대해 토큰 검증 수행 (토큰 추출 ,토큰 존재 확인, 토큰 유효성 검증)
// 토큰 검증 성공 시 사용자 정보를 헤더에 담아 전달 (userId / role 추출, 내부 서비스로 전달)
// 인증 실패 시 요청 차단 (401 Unauthorized)

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        log.info("[Gateway] Request Path: {}, Method: {}", path, method);

        // 로그인 허용
        if (path.equals("/api/auth/login")) {
            log.info("[Gateway] 로그인은 인증 X ");
            return chain.filter(exchange);
        }

        // 회원 가입 허용
        if (path.equals("/api/users") && HttpMethod.POST.equals(method)) {
            log.info("[Gateway] Skip authentication - signup");
            return chain.filter(exchange);
        }

        // Authorization 헤더에서 토큰 추출
        String token = resolveToken(exchange);

        log.info("[Gateway] Extracted Token: {}", token);

        // 토큰 없음 또는 유효하지 않음 → 401
        if (token == null || !jwtProvider.validateToken(token)) {
            log.warn("[Gateway] Invalid or missing token");
            return unauthorized(exchange);
        }

        // 사용자 정보 추출
        String userId = jwtProvider.getUserId(token);
        String role = jwtProvider.getRole(token);

        log.info("[Gateway] Parsed userId: {}, role: {}", userId, role);

        // 내부 서비스로 전달할 헤더 추가
        exchange.getRequest().mutate()
                .header("X-User-Id", userId)
                .header("X-User-Role", role)
                .build();

        log.info("[Gateway] Header set -> X-User-Id: {}, X-User-Role: {}", userId, role);

        // 다음 필터로 전달
        return chain.filter(exchange);
    }

    /**Authorization 헤더에서 Bearer 토큰 추출*/
    private String resolveToken(ServerWebExchange exchange) {
        String bearer = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    /**인증 실패 → 401 Unauthorized*/
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**필터 실행 순서 (가장 먼저 실행)*/
    @Override
    public int getOrder() {
        return -1;
    }
}