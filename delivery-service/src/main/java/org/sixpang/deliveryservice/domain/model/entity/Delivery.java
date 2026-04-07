package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_delivery", schema = "delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.HUB_WAITING;

    @Column(name = "departure_hub", nullable = false)
    private UUID departureHub;

    @Column(name = "arrival_hub", nullable = false)
    private UUID arrivalHub;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(name = "delivery_manager_id", nullable = false)
    private UUID deliveryManagerId;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryRoute> deliveryRoutes = new ArrayList<>();

    @Builder
    public Delivery(UUID orderId, DeliveryStatus status, UUID departureHub,
                    UUID arrivalHub, String address, UUID receiverId,
                    UUID deliveryManagerId) {
        this.orderId = orderId;
        this.status = status != null ? status : DeliveryStatus.HUB_WAITING;
        this.departureHub = departureHub;
        this.arrivalHub = arrivalHub;
        this.address = address;
        this.receiverId = receiverId;
        this.deliveryManagerId = deliveryManagerId;
    }

    // 상태 변경
    public void updateStatus(DeliveryStatus newStatus) {
        this.status = newStatus;
    }

    // 배송 담당자 변경
    public void changeDeliveryManager(UUID newManagerId) {
        this.deliveryManagerId = newManagerId;
    }

    // 경로 추가 헬퍼 메서드
    public void addRoute(DeliveryRoute route) {
        this.deliveryRoutes.add(route);
        route.setDelivery(this);
    }
}
