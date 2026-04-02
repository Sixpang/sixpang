package org.sixpang.userservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponseDto {

    /** 목록 조회 **/
    @Getter
    @AllArgsConstructor
    public static class UserResponse {
        private UUID id;
        private String email;
        private String name;
        private String phone;   // 추가
        private String role;    // 추가
        private String status;
    }

    /** 상세 조회 **/
    @Getter
    @AllArgsConstructor
    public static class UserDetailResponse {
        private UUID id;
        private String email;
        private String name;
        private String phone;
        private String slackId;
        private String role;
        private String status;
        private UUID hubId;
        private UUID companyId;
    }

    /** 회원가입 / 승인 / 상태 변경 **/
    @Getter
    @AllArgsConstructor
    public static class UserSimpleResponse {
        private UUID id;
        private String email;
        private String name;
        private String role;
        private String status;
    }

    /** 회원 수정 **/
    @Getter
    @AllArgsConstructor
    public static class UserUpdateResponse {
        private UUID id;
        private String name;
        private String phone;
        private String role;
        private UUID hubId;
        private UUID companyId;
    }
}