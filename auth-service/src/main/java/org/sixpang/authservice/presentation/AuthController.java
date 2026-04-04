package org.sixpang.authservice.presentation;

import lombok.RequiredArgsConstructor;
import org.sixpang.authservice.application.AuthService;
import org.sixpang.authservice.presentation.dto.LoginRequestDto;
import org.sixpang.authservice.presentation.dto.LoginResponseDto;
import org.sixpang.commonserver.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return ApiResponse.of("로그인 성공", authService.login(request));
    }

    // 로그아웃
    //TODO: 지금은 아무 작업 안 함 (형식만)
    // 나중에 logout →Redis에 토큰 저장 →이 토큰은 인증 거부으로 변경(토큰 무효화)
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {

        return ApiResponse.of("로그아웃 성공", null);
    }

    //TODO:토큰재발급 (나중에구현)
}