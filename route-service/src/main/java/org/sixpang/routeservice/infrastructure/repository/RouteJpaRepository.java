package org.sixpang.routeservice.infrastructure.repository;

import org.sixpang.routeservice.domain.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RouteJpaRepository extends JpaRepository<Route, UUID> {
}
