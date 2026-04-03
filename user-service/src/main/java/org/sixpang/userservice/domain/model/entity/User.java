package org.sixpang.userservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.userservice.domain.model.enums.UserRole;
import org.sixpang.userservice.domain.model.enums.UserStatus;
import org.sixpang.userservice.exception.UserErrorCode;
import org.sixpang.userservice.exception.UserException;

import java.util.UUID;

@Entity
@Table(name = "p_user", schema = "user_service")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private String slackId;

    private UUID hubId;

    private UUID companyId;

    /**생성 메서드**/
    public static User create(
            String email,
            String password,
            String name,
            String phone,
            UserRole role,
            String slackId,
            UUID hubId,
            UUID companyId
    ) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.name = name;
        user.phone = phone;
        user.role = role;
        user.status = UserStatus.PENDING;

        user.slackId = slackId;
        user.hubId = hubId;
        user.companyId = companyId;

        return user;
    }

    /**비지니스 로직**/

    //회원 승인 (규칙: PENDING → APPROVED 가능 / APPROVED → 다시 APPROVED 불가)
    public void approve() {
        if (this.status == UserStatus.APPROVED) {
            throw new UserException(UserErrorCode.ALREADY_APPROVED);
        }
        if (this.status != UserStatus.PENDING) {
            throw new UserException(UserErrorCode.CANNOT_APPROVE);
        }
        this.status = UserStatus.APPROVED;
    }

    //회원 거절(규칙: PENDING → REJECTED 가능 / REJECTED → APPROVED 불가능)
    public void reject() {
        if (this.status == UserStatus.REJECTED) {
            throw new UserException(UserErrorCode.ALREADY_REJECTED);
        }
        this.status = UserStatus.REJECTED;
    }

    // 회원 정보 수정
    public void update(String name, String phone, String slackId) {
        if (name != null) this.name = name;
        if (phone != null) this.phone = phone;
        if (slackId != null) this.slackId = slackId;
    }

    // 비밀번호 변경
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    //회원 (논리적)삭제
    public void delete(UUID deletedBy) {
        super.softDelete(deletedBy);
    }
}