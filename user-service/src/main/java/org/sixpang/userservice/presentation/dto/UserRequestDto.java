package org.sixpang.userservice.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class UserRequestDto {

    @Getter
    @NoArgsConstructor
    public static class SignUpRequest {
        private String email;
        private String password;
        private String name;
        private String phone;
        private String role;
        private String slackId;
        private UUID hubId;
        private UUID companyId;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateUserRequest {
        private String name;
        private String phone;
        private String slackId;
    }

    @Getter
    @NoArgsConstructor
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }
}