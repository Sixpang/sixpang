package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_hub_delivery_manager", schema = "delivery")
public class HubDeliveryManager extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(
            name = "delivery_sequence",
            nullable = true,    // false에서 true로 변경 (어차피 DB SERIAL이 채워줌)
            unique = true,
            updatable = false,
            insertable = false,
            columnDefinition = "SERIAL"
    )
    private Integer deliverySequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryManagerStatus status = DeliveryManagerStatus.WAIT;

    public static HubDeliveryManager create(UUID userId) {
        HubDeliveryManager manager = new HubDeliveryManager();
        manager.userId = userId;
        manager.status = DeliveryManagerStatus.WAIT;
        return manager;
    }

    public void updateStatus(DeliveryManagerStatus Status) {
        this.status = Status;
    }

    @Override
    public void softDelete(UUID deletedBy) {
        super.softDelete(deletedBy); // 부모의 protected 메서드를 호출
    }
}