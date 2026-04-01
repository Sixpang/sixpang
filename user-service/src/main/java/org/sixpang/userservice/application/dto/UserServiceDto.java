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

        private final String email;     // 로그인 ID (수정 불가)
        private final String password;  // 원본 비밀번호 (Service에서 암호화)
        private final String name;      // 사용자 이름
        private final String phone;     // 전화번호 (중복 체크)

        private final UserRole role;    // 사용자 권한

        private final String slackId;   // 선택: 슬랙 ID
        private final UUID hubId;       // 선택: 허브 ID
        private final UUID companyId;   // 선택: 소속 회사 ID
    }

    /**회원 정보 수정 DTO (수정가능한 필드만)**/
    @Getter
    @Builder
    public static class Update {

        private final String name;      // 이름 변경
        private final String phone;     // 전화번호 변경
        private final String slackId;   // 슬랙 ID 변경
    }

    /**비밀번호 변경 DTO**/
    @Getter
    @Builder
    public static class ChangePassword {

        private final String currentPassword; // 현재 비밀번호
        private final String newPassword;     // 새 비밀번호
    }
}