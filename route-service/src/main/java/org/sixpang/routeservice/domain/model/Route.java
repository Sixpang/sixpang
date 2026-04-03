package org.sixpang.routeservice.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_route")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long duration;

    @Column(nullable = false)
    private BigDecimal distance;

    @Builder(access = AccessLevel.PRIVATE)
    private Route(
            Long duration,
            BigDecimal distance
    ){
        this.duration = duration;
        this.distance = distance;
    }

    public static Route of(Long duration, BigDecimal distance){
        return Route.builder()
                .duration(duration)
                .distance(distance)
                .build();
    }
}
