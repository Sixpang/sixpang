package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@DynamicInsert
@Table(name="p_company_delivery_manager", schema = "delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyDeliveryManager extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="hub_id", nullable = false)
    private UUID hubId;

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
    private Integer deliverySequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryManagerStatus status = DeliveryManagerStatus.WAIT;

    public static CompanyDeliveryManager create(UUID userId, UUID hubId){
        CompanyDeliveryManager manager = new CompanyDeliveryManager();
        manager.userId = userId;
        manager.hubId = hubId;
        manager.status = DeliveryManagerStatus.WAIT;
        return manager;
    }

    public void updateStatus(DeliveryManagerStatus Status){
        this.status = Status;
    }

    @Override
    public void softDelete(UUID deletedBy) {
        super.softDelete(deletedBy); // 부모의 protected 메서드를 호출
    }
}
