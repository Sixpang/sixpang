package org.sixpang.productservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.productservice.domain.model.Inventory;
import org.sixpang.productservice.domain.repository.InventoryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryJpaRepository.save(inventory);
    }

    @Override
    public Optional<Inventory> findById(UUID productId) {
        return inventoryJpaRepository.findById(productId);
    }
}
