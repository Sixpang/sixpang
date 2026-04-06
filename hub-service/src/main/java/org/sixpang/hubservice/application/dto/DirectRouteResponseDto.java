package org.sixpang.hubservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.hubservice.domain.model.entity.Route;

import java.math.BigDecimal;

import static org.sixpang.hubservice.infrastructure.RouteFormatter.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DirectRouteResponseDto {
    private BigDecimal distance;
    private Long duration;
    private String distanceKm;
    private String durationMin;

    public static DirectRouteResponseDto from(Route route){
        return DirectRouteResponseDto.builder()
                .distance(route.getDistance())
                .duration(route.getDuration())
                .distanceKm(formatDistance(route.getDistance()))
                .durationMin(formatDuration(route.getDuration()))
                .build();
    }
}
