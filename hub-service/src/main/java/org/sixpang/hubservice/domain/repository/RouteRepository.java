package org.sixpang.hubservice.domain.repository;

import org.sixpang.hubservice.domain.model.entity.Route;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteRepository {
    List<Route> findAllByDepartureHubIdAndDeletedAtIsNull(UUID departureHubId);

    Optional<Route> findByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(UUID departureHubId, UUID arrivalHubId);

    List<Route> findAllByDeletedAtIsNull();

    void softDeleteRoutesByHubId(UUID hubId, UUID userId);

    Route save(Route route);
}
