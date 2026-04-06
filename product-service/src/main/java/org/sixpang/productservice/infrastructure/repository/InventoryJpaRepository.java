package org.sixpang.productservice.infrastructure.repository;

import org.sixpang.productservice.domain.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryJpaRepository extends JpaRepository<Inventory, UUID> {

}
