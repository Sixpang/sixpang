package org.sixpang.hubservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_route", schema = "hub_service")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "departure_hub_name", nullable = false)
    private String departureHubName;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(name = "arrival_hub_name", nullable = false)
    private String arrivalHubName;

    @Column(nullable = false)
    private Long duration;

    @Column(nullable = false)
    private BigDecimal distance;

    @Builder(access = AccessLevel.PRIVATE)
    private Route(
            UUID departureHubId,
            String departureHubName,
            UUID arrivalHubId,
            String arrivalHubName,
            Long duration,
            BigDecimal distance
    ) {
        this.departureHubId = departureHubId;
        this.departureHubName = departureHubName;
        this.arrivalHubId = arrivalHubId;
        this.arrivalHubName = arrivalHubName;
        this.duration = duration;
        this.distance = distance;
    }

    public static Route of(UUID departureHubId, String departureHubName, UUID arrivalHubId, String arrivalHubName, Long duration, BigDecimal distance) {
        return Route.builder()
                .departureHubId(departureHubId)
                .departureHubName(departureHubName)
                .arrivalHubId(arrivalHubId)
                .arrivalHubName(arrivalHubName)
                .duration(duration)
                .distance(distance)
                .build();
    }
}
