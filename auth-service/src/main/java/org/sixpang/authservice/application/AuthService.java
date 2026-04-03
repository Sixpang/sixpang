package org.sixpang.authservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.authservice.infrastructure.client.UserClient;
import org.sixpang.authservice.infrastructure.client.dto.UserAuthDto;
import org.sixpang.authservice.infrastructure.security.JwtProvider;
import org.sixpang.authservice.presentation.dto.LoginRequestDto;
import org.sixpang.authservice.presentation.dto.LoginResponseDto;
import org.sixpang.authservice.exception.AuthErrorCode;
import org.sixpang.authservice.exception.AuthException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /** 로그인 기능 **/
    public LoginResponseDto login(LoginRequestDto request) {

        // 1. 이메일을 기반으로 사용자 인증 정보 조회
        UserAuthDto user = findUser(request.getEmail());

        // 2. 입력한 비밀번호와 저장된 비밀번호 일치 여부 검증
        validatePassword(request.getPassword(), user.getPassword());

        // 3. 사용자 식별 정보로 JWT accessToken 생성
        String accessToken = createAccessToken(user);

        // 4. DTO의 정적 팩토리 메서드를 사용하여 응답 생성
        return LoginResponseDto.of(user, accessToken);
    }


    /** 내부 메서드 **/

    // User 서비스 호출을 통해 사용자 조회
    private UserAuthDto findUser(String email) {
        UserAuthDto user = userClient.getUserByEmail(email).getData();

        if (user == null) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }

    //  비밀번호 일치 여부 검증 (불일치 시 예외 발생)
    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {

            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }
    }

    //  사용자 ID와 권한을 기반으로 accessToken 생성
    private String createAccessToken(UserAuthDto user) {
        return jwtProvider.createToken(user.getId(), user.getRole());
    }
}