package org.sixpang.orderservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_order_item", schema = "order_service")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 주문 아이템 UUID

    private UUID orderId; // 주문 ID
    private UUID productId; // 상품 ID

    private String productName; // 상품명 스냅샷
    private BigDecimal productPrice; // 상품 가격 스냅샷

    @Column(nullable = false)
    private Integer count; // 수량

    // 주문 아이템 생성
    public static OrderItem create(
            UUID orderId,
            UUID productId,
            String productName,
            BigDecimal productPrice,
            Integer count
    ) {
        OrderItem orderItem = new OrderItem();
        orderItem.orderId = orderId;
        orderItem.productId = productId;
        orderItem.productName = productName;
        orderItem.productPrice = productPrice;
        orderItem.count = count;
        return  orderItem;
    }

    // 주문 아이템 수정
    public void update(
            String productName,
            BigDecimal productPrice,
            Integer count
    ) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.count = count;
    }

    // 주문 아이템 취소, 삭제는 주문 ID에 따라 변경 될 텐데
}
