package org.sixpang.userservice.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.userservice.domain.model.enums.UserRole;
import org.sixpang.userservice.domain.model.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserQueryDto {

    /**목록 조회용**/
    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private UUID id;
        private String email;
        private String name;
        private String phone;
        private UserRole role;
        private UserStatus status;
    }

    /**상세 조회용**/
    @Getter
    @AllArgsConstructor
    public static class UserDetail {
        private UUID id;
        private String email;
        private String name;
        private String phone;
        private String slackId;
        private UserRole role;
        private UserStatus status;
        private UUID hubId;
        private UUID companyId;
        private LocalDateTime createdAt;
    }

    /** 로그인용 유저 정보 DTO (비밀번호 포함) **/
    @Getter
    @AllArgsConstructor
    public static class AuthUser {
        private UUID id;
        private String email;
        private String password;
        private UserRole role;
        private UserStatus status;
    }
}
