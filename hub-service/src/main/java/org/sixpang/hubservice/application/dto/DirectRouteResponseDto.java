package org.sixpang.hubservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.hubservice.domain.model.entity.Route;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DirectRouteResponseDto {
    private Long duration;
    private BigDecimal distance;

    public static DirectRouteResponseDto from(Route route){
        return DirectRouteResponseDto.builder()
                .duration(route.getDuration())
                .distance(route.getDistance())
                .build();
    }
}
