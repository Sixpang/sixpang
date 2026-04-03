package org.sixpang.hubservice.infrastructure.repository;

import org.sixpang.hubservice.domain.model.entity.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubJpaRepository extends JpaRepository<Hub, UUID> {
    Optional<Hub> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Hub> findByNameAndDeletedAtIsNull(String name);

    boolean existsByNameAndDeletedAtIsNull(String name);

    Page<Hub> findAllByDeletedAtIsNull(Pageable pageable);
}
