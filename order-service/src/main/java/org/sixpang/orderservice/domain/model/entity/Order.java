package org.sixpang.orderservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_order")
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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    // 주문 생성
    // 생성 시점에 필요한 값 모두 받기
    private Order(
            UUID supplierId,
            UUID receiverId,
            Timestamp deadlineAt,
            BigDecimal totalPrice
    ) {
        this.supplierId = supplierId;
        this.receiverId = receiverId;
        this.deadlineAt = deadlineAt;
        this.totalPrice = totalPrice;
        this.orderStatus = OrderStatus.CONFIRMED; // 주문 상태 (승인)
    }

    // 주문 생성
    public static Order create(UUID supplierId, UUID receiverId, Timestamp deadlineAt, BigDecimal totalPrice) {
        return new Order(supplierId, receiverId, deadlineAt, totalPrice);
    }

    // 기한 수정
    public void update(Timestamp deadlineAt) {
        if (deadlineAt != null) {
            this.deadlineAt = deadlineAt;
        }
    }

    // softDelete
    public void delete(UUID deletedBy) {
        this.softDelete(deletedBy);
    }
}
