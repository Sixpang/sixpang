package org.sixpang.hubservice.infrastructure.repository;

import org.sixpang.hubservice.domain.model.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RouteJpaRepository extends JpaRepository<Route, UUID> {
}
