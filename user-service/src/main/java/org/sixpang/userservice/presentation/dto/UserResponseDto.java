package org.sixpang.userservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

public class UserResponseDto {

    @Getter
    @AllArgsConstructor
    public static class UserResponse {
        private UUID  id;
        private String email;
        private String name;
        private String status;
    }

    @Getter
    @AllArgsConstructor
    public static class UserDetailResponse {
        private UUID id;
        private String email;
        private String name;
        private String phone;
        private String role;
        private String slackId;
        private UUID hubId;
        private UUID  companyId;
        private String status;
    }
}