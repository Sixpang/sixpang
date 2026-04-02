package org.sixpang.hubservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sixpang.hubservice.domain.model.entity.Hub;
import org.sixpang.hubservice.domain.model.enums.HubStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class HubResponseDto {
    private UUID id;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private HubStatus status;

    public static HubResponseDto from(Hub hub){
        return HubResponseDto.builder()
                .id(hub.getId())
                .name(hub.getName())
                .address(hub.getAddress())
                .latitude(hub.getLatitude())
                .longitude(hub.getLongitude())
                .status(hub.getStatus())
                .build();
    }
}
