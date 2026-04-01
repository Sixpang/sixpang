package org.sixpang.hubservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.hubservice.domain.model.enums.HubStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_hub")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hub extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, name = "hub_name", nullable = false)
    private String hubName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HubStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private Hub(
            String hubName,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            HubStatus status
    ) {
        this.hubName = hubName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public static Hub of(String hubName, String address, BigDecimal latitude, BigDecimal longitude, HubStatus status) {
        return Hub.builder()
                .hubName(hubName)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .status(status)
                .build();
    }
}
