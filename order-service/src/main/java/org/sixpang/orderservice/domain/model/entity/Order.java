package org.sixpang.orderservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.ErrorCode;
import org.sixpang.orderservice.domain.model.enums.DeliveryStatus;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_order", schema = "order_service")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 주문 UUID

    private UUID supplierId; // 공급 업체 ID
    private UUID receiverId; // 수령 업체 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus; // 주문 상태

    @Column(nullable = false, name = "deadline_at")
    private Timestamp deadlineAt; // 납입 기한 일자+시간

    @Column(nullable = false, name = "total_price")
    private BigDecimal totalPrice; // 주문 총 액

    @Column(nullable = false, name = "delivery_status")
    private DeliveryStatus deliveryStatus; // 배송 상태

    // 주문 생성
    public static Order create(
            UUID supplierId,
            UUID receiverId,
            Timestamp deadlineAt,
            BigDecimal totalPrice
    ) {
        Order order = new Order();
        order.supplierId = supplierId;
        order.receiverId = receiverId;
        order.deadlineAt = deadlineAt;
        order.totalPrice = totalPrice;
        order.orderStatus = OrderStatus.CONFIRMED; // 기본 값 : 승인
        order.deliveryStatus = DeliveryStatus.PENDING; // 기본 값 : 배송 예정
        return  order;
    }

    // 주문 수정
    public void update(
            Timestamp deadlineAt,
            BigDecimal totalPrice
    ) {
        this.deadlineAt = deadlineAt;
        this.totalPrice = totalPrice;
    }

    // 주문 취소
    public void cancel( ) {
        if (this.orderStatus == OrderStatus.CANCELLED){
            throw new IllegalArgumentException("이미 취소 된 주문입니다.");
        }
        if (this.deliveryStatus == DeliveryStatus.DELIVERING) {
            throw  new IllegalArgumentException("배송 중인 주문은 취소할 수 없습니다.");
        }
        this.orderStatus = OrderStatus.CANCELLED;
        this.deliveryStatus = DeliveryStatus.PENDING;
    }

    // 주문 삭제
    public  void delete(UUID id) {
        super.softDelete(id);
    }
}
