package org.sixpang.productservice.domain.repository;

import org.sixpang.productservice.domain.model.Inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {

    Inventory save(Inventory inventory);
    Optional<Inventory> findById(UUID productId);
}
