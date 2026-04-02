package org.sixpang.deliveryservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
## 배송 생성 요구사항
0. 배송 생성은 주문생성과 동시에 생성된다.
1. 배송 생성은 마스터만 가능하다
2. 배송 생성과 함께 배송 경로가 생성되며 배송 경로가 생성 되어야 배송 생성이 완료된다(실패시 롤백).
3. 배송 생성시 출발 및 목적 허브 ID가 존재하는지 확인한다.

## 배송 조회 요구사항
0. 각 조회 권한은 아래와 같다.
    마스터: 모든 조회 가능
    허브 관리자: 담당 허브에 한해 가능
    배송 담당자: 본인 배송에 한해 가능
    업체 관리자: 본인 건에 한해 가능
1. 배송 과정에서 발생한 각 경로 추적이 가능해야 한다.

## 배송 수정 요구사항
0. 각 수정 권한은 아래와 같다.
    마스터: 모든 건 수정 가능
    허브 관리자: 담당 허브에 한해 가능
    배송 담당자: 본인 배송에 한해 가능
    업체 관리자: 본인 건에 한해 가능
1. 배송 상태
*
* */
@Entity
@Getter
@Table(name="p_delivery", schema = "delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name="order_id", nullable=false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.HUB_WAITING;

    @Column(name="departure_hub", nullable=false)
    private UUID departureHub;

    @Column(name="arrival_hub", nullable=false)
    private UUID arrivalHub;

    @Column(nullable=false, length = 255)
    private String address;

    @Column(name="receiver_id", nullable=false)
    private UUID receiverId;

    @Column(name="delivery_manager_id", nullable=false)
    private UUID deliveryManagerId;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryRoute> deliveryRoutes = new ArrayList<>();
}
