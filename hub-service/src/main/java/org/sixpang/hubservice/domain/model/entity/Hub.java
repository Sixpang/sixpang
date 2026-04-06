package org.sixpang.hubservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.hubservice.domain.model.enums.HubStatus;
import org.sixpang.hubservice.exception.HubErrorCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_hub", schema = "hub")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hub extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, name = "hub_name", nullable = false)
    private String name;

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
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            HubStatus status
    ) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public static Hub of(String name, String address, BigDecimal latitude, BigDecimal longitude, HubStatus status) {
        return Hub.builder()
                .name(name)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .status(status)
                .build();
    }

    // 정보 수정
    public void updateInfo(String name, String address, BigDecimal latitude, BigDecimal longitude, HubStatus status) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    // 삭제
    public void delete(UUID id) {
        if (this.isDeleted()) {
            throw new CustomException(HubErrorCode.HUB_ALREADY_DELETED);
        }
        this.softDelete(id);
    }
}
