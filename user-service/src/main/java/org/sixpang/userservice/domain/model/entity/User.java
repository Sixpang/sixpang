package org.sixpang.userservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.userservice.domain.model.enums.UserRole;
import org.sixpang.userservice.domain.model.enums.UserStatus;

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

    //==생성 메서드==
    public static User create(
            String email,
            String password,
            String name,
            String phone,
            UserRole role
    ) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.name = name;
        user.phone = phone;
        user.role = role;
        user.status = UserStatus.PENDING; //기본값 : 대기
        return user;
    }


    //====비즈니스 메서드(최소기능)====
    //TODO: 추후 공통에러형식으로 방어 로직 생성 및 메서드 추가

    //회원 승인
    public void approve() {
        this.status = UserStatus.APPROVED;
    }
    //회원 거절
    public void reject() {
        this.status = UserStatus.REJECTED;
    }
}