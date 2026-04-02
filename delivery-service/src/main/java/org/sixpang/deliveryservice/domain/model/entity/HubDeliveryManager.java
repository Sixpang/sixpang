package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="p_hub_delivery_manager", schema = "delivery")
public class HubDeliveryManager{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="hub_id", nullable = false)
    private UUID hubId;

    //
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(
            name = "delivery_sequence",
            nullable = false,
            unique = true,
            updatable = false,
            insertable = false, //DB에서 생성하게 두기 위해 false 설정
            columnDefinition = "SERIAL"
    )
    private Long deliverySequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryManagerStatus status = DeliveryManagerStatus.WAIT;

    public static HubDeliveryManager create(UUID userId, UUID hubId){
        HubDeliveryManager manager = new HubDeliveryManager();
        manager.userId = userId;
        manager.hubId = hubId;
        manager.status = DeliveryManagerStatus.WAIT;
        return manager;
    }

    public void updateStatus(DeliveryManagerStatus Status){
        this.status = Status;
    }
}

}