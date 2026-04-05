package org.sixpang.productservice.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;

import java.util.UUID;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_inventory")
public class Inventory extends BaseEntity {

    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    private void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("수량은 1 이상이어야 합니다.");
        }
    }


    public Inventory(UUID productId, Long quantity) {
        if (quantity == null || quantity < 0) {
            throw new RuntimeException("재고 수량은 0 이상이어야 합니다.");
        }
        this.productId = productId;
        this.quantity = quantity;
    }

    public void increase(Long amount) {
        validateAmount(amount);
        this.quantity += amount;
    }

    public void decrease(Long amount) {
        validateAmount(amount);

        if (this.quantity < amount) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        this.quantity -= amount;
    }

}
