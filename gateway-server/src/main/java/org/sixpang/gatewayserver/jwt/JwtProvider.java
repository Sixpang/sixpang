package org.sixpang.gatewayserver.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtProvider {


    private final String secretKey = "my-secret-key-my-secret-key-my-secret-key";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**토큰 유효성 검증*/
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
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
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}