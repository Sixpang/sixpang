package org.sixpang.productservice.domain.repository;

import org.sixpang.productservice.application.dto.ProductSearchRequest;
import org.sixpang.productservice.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findByIdAndDeletedAtIsNull(UUID productId);
    Boolean existsByNameAndDeletedAtIsNull(String name);
    Page<Product> searchProducts(ProductSearchRequest request, Pageable pageable);
}
