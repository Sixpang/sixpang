package org.sixpang.authservice.presentation;

import lombok.RequiredArgsConstructor;
import org.sixpang.authservice.application.AuthService;
import org.sixpang.authservice.presentation.dto.LoginRequestDto;
import org.sixpang.authservice.presentation.dto.LoginResponseDto;
import org.sixpang.commonserver.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    // 로그인
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다."
    )
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return ApiResponse.of("로그인 성공", authService.login(request));
    }

    // 로그아웃
    //TODO: logout →Redis에 토큰 저장 →이 토큰은 인증 거부으로 변경(토큰 무효화)
    @Operation(
            summary = "로그아웃",
            description = "(미구현) Authorization 헤더의 토큰으로 로그아웃합니다. 현재는 실제 토큰 무효화는 동작하지 않습니다."
    )
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {

        return ApiResponse.of("로그아웃 성공", null);
    }

    //TODO:토큰재발급 (추후 구현)
}