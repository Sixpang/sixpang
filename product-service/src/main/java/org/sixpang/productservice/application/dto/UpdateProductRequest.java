package org.sixpang.productservice.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest (
        String name,
        BigDecimal price,
        UUID companyId,
        UUID hubId
){
}
