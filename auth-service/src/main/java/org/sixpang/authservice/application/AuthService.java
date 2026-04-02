package org.sixpang.authservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.authservice.infrastructure.client.UserClient;
import org.sixpang.authservice.infrastructure.client.dto.UserAuthDto;
import org.sixpang.authservice.infrastructure.security.JwtProvider;
import org.sixpang.authservice.presentation.dto.LoginRequestDto;
import org.sixpang.authservice.presentation.dto.LoginResponseDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponseDto login(LoginRequestDto request) {

        // 1. 유저 조회 (User 서비스 호출)
        UserAuthDto user = userClient.getUserByEmail(request.getEmail());

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 3. JWT 생성
        String accessToken = jwtProvider.createToken(user.getId(), user.getRole());

        // 4. 응답 생성
        return new LoginResponseDto(
                accessToken,
                null, // refreshToken (나중에 Redis 붙일 때)
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}