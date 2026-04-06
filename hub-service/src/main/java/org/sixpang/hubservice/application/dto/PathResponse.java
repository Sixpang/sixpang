package org.sixpang.hubservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.hubservice.domain.model.entity.Route;

import java.math.BigDecimal;
import java.util.UUID;

import static org.sixpang.hubservice.infrastructure.RouteFormatter.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PathResponse {
    private int sequence;
    private UUID departureHubId;
    private String departureHubName;
    private BigDecimal distance;
    private Long duration;
    private String distanceKm;
    private String durationMin;

    public static PathResponse from(int sequence, Route route){
        return PathResponse.builder()
                .sequence(sequence)
                .departureHubId(route.getDepartureHubId())
                .departureHubName(route.getDepartureHubName())
                .distance(route.getDistance())
                .duration(route.getDuration())
                .distanceKm(formatDistance(route.getDistance()))
                .durationMin(formatDuration(route.getDuration()))
                .build();
    }
}
