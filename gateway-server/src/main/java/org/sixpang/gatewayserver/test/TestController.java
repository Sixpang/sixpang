package org.sixpang.gatewayserver.test;

import lombok.RequiredArgsConstructor;
import org.sixpang.gatewayserver.jwt.JwtProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final JwtProvider jwtProvider;

    @GetMapping("/test")
    public String test(@RequestHeader("Authorization") String header) {

        String token = header.replace("Bearer ", "");

        boolean valid = jwtProvider.validateToken(token);
        String userId = jwtProvider.getUserId(token);
        String role = jwtProvider.getRole(token);

        return "valid=" + valid + ", userId=" + userId + ", role=" + role;
    }
}