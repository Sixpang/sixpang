package org.sixpang.productservice.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_product",schema = "product_service")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID id;

    @Column(name = "product_name", nullable = false, length = 255)
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "hub_id", nullable = false)
    private UUID hubId;

    public Product(String name, BigDecimal price, UUID companyId, UUID hubId) {
        this.name = name;
        this.price = price;
        this.companyId = companyId;
        this.hubId = hubId;
    }

    public void update(String name, BigDecimal price, UUID companyId, UUID hubId) {
        if (name != null) {
            this.name = name;
        }
        if (price != null) {
            this.price = price;
        }
        if (companyId != null) {
            this.companyId = companyId;
        }
        if (hubId != null) {
            this.hubId = hubId;
        }
    }

    //삭제자 UUID를 받을 수 있을때 수정
    public void delete() {
        softDelete();
    }
}
