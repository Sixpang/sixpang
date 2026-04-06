package org.sixpang.hubservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptimalRouteResponseDto {
    private BigDecimal totalDistance;
    private Long totalDuration;
    private List<PathResponse> pathList;

    private static OptimalRouteResponseDto from(BigDecimal totalDistance, Long totalDuration, List<PathResponse> pathList){
        return OptimalRouteResponseDto.builder()
                .totalDistance(totalDistance)
                .totalDuration(totalDuration)
                .pathList(pathList)
                .build();
    }
}
