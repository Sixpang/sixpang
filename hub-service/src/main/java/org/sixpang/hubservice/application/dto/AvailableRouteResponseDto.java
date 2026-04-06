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
public class AvailableRouteResponseDto {
    private UUID arrivalHubId;
    private String arrivalHubName;
    private BigDecimal distance;
    private Long duration;
    private String distanceKm;
    private String durationMin;

    public static AvailableRouteResponseDto from(Route route){
        return AvailableRouteResponseDto.builder()
                .arrivalHubId(route.getArrivalHubId())
                .arrivalHubName(route.getArrivalHubName())
                .distance(route.getDistance())
                .duration(route.getDuration())
                .distanceKm(formatDistance(route.getDistance()))
                .durationMin(formatDuration(route.getDuration()))
                .build();
    }
}
