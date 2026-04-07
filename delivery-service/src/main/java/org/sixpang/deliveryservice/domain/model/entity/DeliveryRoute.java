package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.DeliveryRouteCreateCommand;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryRouteStatus;
import org.sixpang.deliveryservice.exception.DeliveryRouteErrorCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_delivery_route", schema = "delivery")
public class DeliveryRoute extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(nullable = false)
    private Integer hubSequence;

    @Column(name = "departure_hub", nullable = false)
    private UUID departureHub;

    @Column(name = "arrival_hub", nullable = false)
    private UUID arrivalHub;

    @Column(name = "hub_route_id", nullable = false)
    private UUID hubRouteId;

    @Column(nullable = false)
    private BigDecimal estimatedDistance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryRouteStatus status;

    @Column(name = "hub_delivery_manager_id", nullable = false)
    private UUID hubDeliveryManagerId;

    @Column(nullable = false)
    private Long estimatedTime;

    @Column(name = "actual_at")
    private LocalDateTime actualAt;

    //정적 팩토리 메서드
    public static DeliveryRoute createHubRoute(DeliveryRouteCreateCommand command) {
        validateRouteData(command);

        DeliveryRoute route = new DeliveryRoute();
        route.delivery = command.deliveryId();
        route.hubSequence = command.hubSequence();
        route.departureHub = command.departureHub();
        route.arrivalHub = command.arrivalHub();
        route.hubRouteId = command.hubRouteId();
        route.estimatedDistance = command.estimatedDistance();
        route.estimatedTime = command.estimatedTime();
        route.hubDeliveryManagerId = command.hubDeliveryManagerId();
        route.status = DeliveryRouteStatus.HUB_WAITING;

        return route;
    }

    @Builder
    public DeliveryRoute(Delivery delivery, Integer hubSequence, UUID departureHub,
                         UUID arrivalHub, UUID hubRouteId, BigDecimal estimatedDistance,
                         Long estimatedTime, UUID hubDeliveryManagerId) {
        this.delivery = delivery;
        this.hubSequence = hubSequence;
        this.departureHub = departureHub;
        this.arrivalHub = arrivalHub;
        this.hubRouteId = hubRouteId;
        this.estimatedDistance = estimatedDistance;
        this.estimatedTime = estimatedTime;
        this.hubDeliveryManagerId = hubDeliveryManagerId;
        this.status = DeliveryRouteStatus.HUB_WAITING;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public void updateStatus(DeliveryRouteStatus status) {
        this.status = status;
    }

    public void completeDelivery() {
        this.actualAt = LocalDateTime.now();
        this.status = DeliveryRouteStatus.DONE;
    }


    private static void validateRouteData(DeliveryRouteCreateCommand command) {
        checkPositive(command.hubSequence(), DeliveryRouteErrorCode.INVALID_ROUTE_SEQUENCE);
        checkPositive(command.estimatedDistance(), DeliveryRouteErrorCode.INVALID_ROUTE_DISTANCE);
        checkPositive(command.estimatedTime(), DeliveryRouteErrorCode.INVALID_ROUTE_TIME);
    }

    private static void checkPositive(Integer value, DeliveryRouteErrorCode errorCode) {
        if (value == null) {
            throw new CustomException(errorCode);
        }
        if (value < 0) {
            throw new CustomException(errorCode);
        }
    }

    private static void checkPositive(Long value, DeliveryRouteErrorCode errorCode) {
        if (value == null) {
            throw new CustomException(errorCode);
        }
        if (value < 0) {
            throw new CustomException(errorCode);
        }
    }

    private static void checkPositive(BigDecimal value, DeliveryRouteErrorCode errorCode) {
        if (value == null) {
            throw new CustomException(errorCode);
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(errorCode);
        }
    }
}
