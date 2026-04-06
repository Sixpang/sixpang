package org.sixpang.userservice.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.commonserver.enums.UserRole;
import org.sixpang.userservice.domain.model.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**상세 조회용**/
@Getter
@AllArgsConstructor
public class UserDetail {

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