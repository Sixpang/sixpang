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

import java.nio.charset.StandardCharsets;

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

        log.info("[Gateway] {} {}", method, path);

        // 로그인 허용
        if (path.equals("/api/auth/login")) {
            return chain.filter(exchange);
        }

        // 회원 가입 허용
        if (path.equals("/api/users") && HttpMethod.POST.equals(method)) {
            return chain.filter(exchange);
        }

        // Authorization 헤더에서 토큰 추출
        String token = resolveToken(exchange);

        // 토큰 없음 → 401 (이유 포함)
        if (token == null) {
            log.warn("[Gateway] 토큰 없음");
            return unauthorized(exchange, "토큰이 없습니다");
        }

        // 토큰 유효성 실패 → 401 (이유 포함)
        if (!jwtProvider.validateToken(token)) {
            log.warn("[Gateway] 유효하지 않은 토큰");
            return unauthorized(exchange, "유효하지 않은 토큰입니다");
        }

        // 사용자 정보 추출
        String userId = jwtProvider.getUserId(token);
        String role = jwtProvider.getRole(token);

        // 요청에 헤더를 추가
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Role", role)
                        .build())
                .build();

        // 디버깅 로그
        log.info("[Gateway] userId={}, role={}", userId, role);
        log.info("[Gateway] headers → X-User-Id={}, X-User-Role={}",
                mutatedExchange.getRequest().getHeaders().getFirst("X-User-Id"),
                mutatedExchange.getRequest().getHeaders().getFirst("X-User-Role")
        );

        return chain.filter(mutatedExchange);
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
    //(코드리뷰 :실패 이유가 안들어온다 들어와야함 검증하는 쪽에서 상세하게 예외처리를 잡아라)
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes))
        );
    }

    /**필터 실행 순서 (가장 먼저 실행)*/
    @Override
    public int getOrder() {
        return -1;
    }
}