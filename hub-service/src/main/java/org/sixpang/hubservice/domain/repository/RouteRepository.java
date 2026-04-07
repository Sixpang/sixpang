package org.sixpang.hubservice.domain.repository;

import org.sixpang.hubservice.domain.model.entity.Route;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteRepository {
    Optional<Route> findByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(UUID departureHubId, UUID arrivalHubId);

    List<Route> findAllActiveRoutes();

    List<Route> findAllActiveRoutesByDepartureHubId(UUID departureHubId);

    boolean existsByDepartureHubIdAndArrivalHubId(UUID departureHubId, UUID arrivalHubId);

    void softDeleteRoutesByHubId(UUID hubId, UUID userId);

    Route save(Route route);
}
