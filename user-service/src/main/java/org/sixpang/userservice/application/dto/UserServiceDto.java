package org.sixpang.userservice.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.userservice.domain.model.enums.UserRole;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceDto {

    /**회원가입 DTO**/
    @Getter
    @Builder
    public static class SignUp {

        private final String email;
        private final String password;
        private final String name;
        private final String phone;

        private final UserRole role;

        private final String slackId;
        private final UUID hubId;
        private final UUID companyId;
    }

    /**회원 정보 수정 DTO (수정가능한 필드만)**/
    @Getter
    @Builder
    public static class Update {

        private final String name;
        private final String phone;
        private final String slackId;
    }

    /**비밀번호 변경 DTO**/
    @Getter
    @Builder
    public static class ChangePassword {

        private final String currentPassword;
        private final String newPassword;
    }
}