package org.sixpang.productservice.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSearchRequest(
        String name,
        BigDecimal price,
        UUID companyId,
        UUID hubId
) {
}
