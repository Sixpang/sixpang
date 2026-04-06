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
public class PathResponse {
    private int sequence;
    private UUID departureHubId;
    private String departureHubName;
    private BigDecimal distance;
    private Long duration;

    public static PathResponse from(int sequence, Route route){
        return PathResponse.builder()
                .sequence(sequence)
                .departureHubId(route.getDepartureHubId())
                .departureHubName(route.getDepartureHubName())
                .distance(route.getDistance())
                .duration(route.getDuration())
                .build();
    }
}
