package org.sixpang.userservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.userservice.domain.model.enums.UserStatus;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_user_status_history", schema = "user_service")
public class UserStatusHistory extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String reason;

    public UserStatusHistory(UUID userId, UserStatus status, String reason) {
        this.userId = userId;
        this.status = status;
        this.reason = reason;
    }
}