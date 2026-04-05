package org.sixpang.userservice.domain.model.entity;

import jakarta.persistence.*;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.userservice.domain.model.enums.UserStatus;

import java.util.UUID;

@Entity
public class UserStatusHistory extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String reason;
}