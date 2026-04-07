package org.sixpang.hubservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static org.sixpang.hubservice.infrastructure.RouteFormatter.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptimalRouteResponseDto {
    private BigDecimal totalDistance;
    private Long totalDuration;
    private String distanceKm;
    private String durationMin;
    private List<PathResponse> pathList;

    public static OptimalRouteResponseDto from(BigDecimal totalDistance, Long totalDuration, List<PathResponse> pathList){
        return OptimalRouteResponseDto.builder()
                .totalDistance(totalDistance)
                .totalDuration(totalDuration)
                .distanceKm(formatDistance(totalDistance))
                .durationMin(formatDuration(totalDuration))
                .pathList(pathList)
                .build();
    }
}
