package org.sixpang.userservice.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.model.enums.UserRole;
import org.sixpang.userservice.domain.model.enums.UserStatus;

import java.util.UUID;

/** 로그인용 유저 정보 DTO (비밀번호 포함) **/
@Getter
@AllArgsConstructor
public class AuthUser {

    private UUID id;
    private String email;
    private String password;
    private UserRole role;
    private UserStatus status;
    private String name;

    // 코드리뷰: DTO 변환 책임을 DTO로 위임
    public static AuthUser from(User user) {
        return new AuthUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getStatus(),
                user.getName()
        );
    }
}