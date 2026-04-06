package org.sixpang.productservice.infrastructure.repository;

import org.sixpang.productservice.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<Product, UUID>,ProductJpaRepositoryCustom {

    Optional<Product> findByIdAndDeletedAtIsNull(UUID productId);
    Boolean existsByNameAndDeletedAtIsNull(String name);
}
