package org.sixpang.gatewayserver.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**토큰 유효성 검증*/
    // (코드 리뷰: 토큰에대한 검증이 부족한 상태)
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);

            // (코드 리뷰: 토큰 만료(exp) 검증)
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            // (코드 리뷰: 우리 시스템에서 발급한 토큰인지 검증 (issuer))
            if (!issuer.equals(claims.getIssuer())) {
                return false;
            }

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**userId 추출*/
    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    /**role 추출*/
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**공통 파싱 로직*/
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}