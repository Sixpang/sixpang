package org.sixpang.hubservice.domain.repository;

import org.sixpang.hubservice.domain.model.entity.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface HubRepository {
    Optional<Hub> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Hub> findByNameAndDeletedAtIsNull(String name);

    boolean existsByNameAndDeletedAtIsNull(String name);

    Page<Hub> findAllByDeletedAtIsNull(Pageable pageable);

    Hub save(Hub hub);
}
