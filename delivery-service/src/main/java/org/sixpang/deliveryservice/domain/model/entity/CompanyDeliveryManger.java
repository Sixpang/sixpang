package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryStatus;

import java.util.UUID;

@Entity
@Getter
@Table(name="p_company_delivery_manager", schema = "delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyDeliveryManger{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    public static CompanyDeliveryManger create(UUID userId){
        CompanyDeliveryManger manager = new CompanyDeliveryManger();
        manager.userId = userId;
        manager.status = DeliveryManagerStatus.WAIT;
        return manager;
    }

    public void updateStatus(DeliveryManagerStatus Status){
        this.status = Status;
    }
}
