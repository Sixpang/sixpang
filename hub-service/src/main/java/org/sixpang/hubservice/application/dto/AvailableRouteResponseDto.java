package org.sixpang.hubservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.hubservice.domain.model.entity.Route;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableRouteResponseDto {
    private UUID arrivalHubId;
    private String arrivalHubName;
    private Long duration;
    private BigDecimal distance;

    public static AvailableRouteResponseDto from(Route route){
        return AvailableRouteResponseDto.builder()
                .arrivalHubId(route.getArrivalHubId())
                .arrivalHubName(route.getArrivalHubName())
                .duration(route.getDuration())
                .distance(route.getDistance())
                .build();
    }
}
