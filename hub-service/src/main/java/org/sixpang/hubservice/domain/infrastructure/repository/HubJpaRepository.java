package org.sixpang.hubservice.domain.infrastructure.repository;

import org.sixpang.hubservice.domain.model.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubJpaRepository  extends JpaRepository<Hub, UUID> {
}
