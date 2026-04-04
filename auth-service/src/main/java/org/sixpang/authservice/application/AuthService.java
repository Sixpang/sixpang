package org.sixpang.authservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.authservice.application.client.UserClient;
import org.sixpang.authservice.application.client.dto.UserAuthDto;
import org.sixpang.authservice.application.security.TokenService;
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

    //(코드리뷰:JwtProvider 구현체가 아닌 인터페이스에 의존하도록 수정)
    private final TokenService tokenService;

    /** 로그인 기능 **/
    // 1. 이메일을 기반으로 사용자 인증 정보 조회
    // 2. 입력한 비밀번호와 저장된 비밀번호 일치 여부 검증
    // 3. 사용자 식별 정보로 JWT 토큰 생성
    // 4. DTO의 정적 팩토리 메서드를 사용하여 응답 생성
    public LoginResponseDto login(LoginRequestDto request) {

        UserAuthDto user = findUser(request.getEmail());
        validatePassword(request.getPassword(), user.getPassword());
        String accessToken = createAccessToken(user);
        return LoginResponseDto.of(user, accessToken);
    }


    /** 내부 메서드 **/
    // User 서비스 호출을 통해 사용자 조회
    // (코드 리뷰:FeignClient 를 직접 쓰는게 아니라 UserClient 인터페이스만 본다 (구현은 구현체에서))
    private UserAuthDto findUser(String email) {
        UserAuthDto user = userClient.getUserByEmail(email);

        if (user == null) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }

    //  비밀번호 일치 여부 검증 (불일치 시 예외 발생)
    // (코드 리뷰 :메서드로 분리 해서 가독성 개선)
    private void validatePassword(String rawPassword, String encodedPassword) {
        if (isPasswordNotMatched(rawPassword, encodedPassword)) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }
    }

    private boolean isPasswordNotMatched(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }

    //  사용자 ID와 권한을 기반으로 accessToken 생성
    private String createAccessToken(UserAuthDto user) {
        return tokenService.createToken(user.getId(), user.getRole());
    }
}