package org.sixpang.productservice.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.productservice.infrastructure.exception.ProductErrorCode;

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
            throw new CustomException(ProductErrorCode.INVALID_INVENTORY_QUANTITY);
        }
    }

    public Inventory(UUID productId){
        this.productId = productId;
        this.quantity = 0L;
    }

    public void increase(Long amount) {
        validateAmount(amount);
        this.quantity += amount;
    }

    public void decrease(Long amount) {
        validateAmount(amount);

        if (this.quantity < amount) {
            throw new CustomException(ProductErrorCode.INSUFFICIENT_INVENTORY);
        }
        this.quantity -= amount;
    }

}
