package org.sixpang.hubservice.domain.repository;

import org.sixpang.hubservice.domain.model.entity.Hub;
import org.sixpang.hubservice.domain.model.enums.HubStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository {
    Optional<Hub> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByNameAndDeletedAtIsNull(String name);

    Page<Hub> findAllByDeletedAtIsNull(Pageable pageable);

    List<Hub> findAllByDeletedAtIsNull();

    List<Hub> findAllByStatus(HubStatus status);

    Hub save(Hub hub);
}
