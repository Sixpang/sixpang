package org.sixpang.productservice.infrastructure.repository;

import org.sixpang.productservice.application.dto.ProductSearchRequest;
import org.sixpang.productservice.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductJpaRepositoryCustom {
    Page<Product> searchProducts(ProductSearchRequest request, Pageable pageable);
}
