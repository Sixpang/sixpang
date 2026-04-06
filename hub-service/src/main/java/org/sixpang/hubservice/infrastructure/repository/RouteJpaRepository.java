package org.sixpang.hubservice.infrastructure.repository;

import org.sixpang.hubservice.domain.model.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteJpaRepository extends JpaRepository<Route, UUID> {
    List<Route> findAllByDepartureHubIdAndDeletedAtIsNull(UUID departureHubId);

    Optional<Route> findByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(UUID departureHubId, UUID arrivalHubId);

    List<Route> findAllByDeletedAtIsNull();

    @Modifying
    @Query("UPDATE Route r SET r.deletedAt = CURRENT_TIMESTAMP, r.deletedBy = :userId " +
            "WHERE (r.departureHubId = :hubId OR r.arrivalHubId = :hubId) AND r.deletedAt IS NULL")
    void softDeleteRoutesByHubId(@Param("hubId") UUID hubId, @Param("userId") UUID userId);
}
