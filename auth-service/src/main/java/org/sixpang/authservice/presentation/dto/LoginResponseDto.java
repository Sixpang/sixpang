package org.sixpang.authservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.authservice.application.client.dto.UserAuthDto;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    //로그인 성공 시 클라이언트에 반환되는 응답 DTO
    private String accessToken;
    private String refreshToken;
    private String id;
    private String email;
    private String name;
    private String role;

    //사용자 정보와 accessToken을 기반으로 로그인 응답 DTO를 생성
    public static LoginResponseDto of(UserAuthDto user, String accessToken) {
        return new LoginResponseDto(
                accessToken,
                null,
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }

}