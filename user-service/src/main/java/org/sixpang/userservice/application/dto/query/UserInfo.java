package org.sixpang.userservice.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.model.enums.UserRole;
import org.sixpang.userservice.domain.model.enums.UserStatus;

import java.util.UUID;

/**목록 조회용**/
@Getter
@AllArgsConstructor
public class UserInfo {

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