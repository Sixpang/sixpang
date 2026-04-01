package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.sixpang.deliveryservice.domain.model.enums.DeliveryRouteStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Table(name="p_delivery_route", schema = "delivery")
public class DeliveryRoute {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(nullable = false)
    private Integer hubSequence;

    @Column(name="departure_hub", nullable=false)
    private UUID departureHub;

    @Column(name="arrival_hub", nullable=false)
    private UUID arrivalHub;

    @Column(name="hub_route_id", nullable=false)
    private UUID hubRouteId;

    @Column(nullable = false)
    private Integer estimatedDistance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryRouteStatus status;

    @Column(name="hub_delivery_manager_id", nullable=false)
    private UUID hubDeliveryManagerId;

    @Column(nullable = false)
    private Integer estimatedTime;

    @Column(name = "actual_at")
    private LocalDateTime actualAt;
}
