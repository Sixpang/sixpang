package org.sixpang.deliveryservice.domain;

import jakarta.persistence.*;

import java.util.UUID;

@MappedSuperclass
public abstract class DeliveryBaseEntity{
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(
            name = "delivery_sequence",
            nullable = false,
            unique = true,
            updatable = false,
            insertable = false, // DB에서 생성하게 두기 위해 false 설정
            columnDefinition = "SERIAL" // PostgreSQL의 경우 SERIAL 명시
    )
    private Long deliverySequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryManagerStatus status = DeliveryManagerStatus.WAIT;
}