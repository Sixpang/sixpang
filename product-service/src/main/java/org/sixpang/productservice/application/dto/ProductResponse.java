package org.sixpang.productservice.application.dto;

import org.sixpang.productservice.domain.model.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String name,
        BigDecimal price,
        UUID companyId,
        UUID hubId
) {
    public static ProductResponse from(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCompanyId(),
                product.getHubId()
        );
    }
}
