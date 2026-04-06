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
@Table(name = "p_route")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(nullable = false)
    private Long duration;

    @Column(nullable = false)
    private BigDecimal distance;

    @Builder(access = AccessLevel.PRIVATE)
    private Route(
            UUID departureHubId,
            UUID arrivalHubId,
            Long duration,
            BigDecimal distance
    ){
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
        this.duration = duration;
        this.distance = distance;
    }

    public static Route of(UUID departureHubId, UUID arrivalHubId, Long duration, BigDecimal distance){
        return Route.builder()
                .departureHubId(departureHubId)
                .arrivalHubId(arrivalHubId)
                .duration(duration)
                .distance(distance)
                .build();
    }
}
