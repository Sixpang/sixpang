package org.sixpang.userservice.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.userservice.domain.model.entity.User;
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

        // 코드리뷰: DTO가 Entity → DTO 변환 책임을 갖도록 from 메서드 추가
        public static UserInfo from(User user) {
            return new UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhone(),
                    user.getRole(),
                    user.getStatus()
            );
        }
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

        // 코드리뷰: 객체 생성 책임을 DTO 내부로 이동
        public static UserDetail from(User user) {
            return new UserDetail(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhone(),
                    user.getSlackId(),
                    user.getRole(),
                    user.getStatus(),
                    user.getHubId(),
                    user.getCompanyId(),
                    user.getCreatedAt()
            );
        }
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

        // 코드리뷰: DTO 변환 책임을 DTO로 위임
        public static AuthUser from(User user) {
            return new AuthUser(
                    user.getId(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getRole(),
                    user.getStatus()
            );
        }
    }
}