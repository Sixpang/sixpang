package org.sixpang.productservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.productservice.application.dto.ProductSearchRequest;
import org.sixpang.productservice.domain.model.Product;
import org.sixpang.productservice.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findByIdAndDeletedAtIsNull(UUID productId) {
        return productJpaRepository.findByIdAndDeletedAtIsNull(productId);
    }

    @Override
    public Boolean existsByNameAndDeletedAtIsNull(String name) {
        return productJpaRepository.existsByNameAndDeletedAtIsNull(name);
    }

    @Override
    public Page<Product> searchProducts(ProductSearchRequest request, Pageable pageable) {
        return productJpaRepository.searchProducts(request,pageable);
    }
}
