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
    Optional<Route> findByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(UUID departureHubId, UUID arrivalHubId);

    boolean existsByDepartureHubIdAndArrivalHubId(UUID departureHubId, UUID arrivalHubId);

    @Query("SELECT r FROM Route r " +
            "JOIN Hub startHub ON r.departureHubId = startHub.id " +
            "JOIN Hub endHub ON r.arrivalHubId = endHub.id " +
            "WHERE r.deletedAt IS NULL " +
            "AND startHub.deletedAt IS NULL AND startHub.status = 'ACTIVE' " +
            "AND endHub.deletedAt IS NULL AND endHub.status = 'ACTIVE'")
    List<Route> findAllActiveRoutes();

    @Query("SELECT r FROM Route r " +
            "JOIN Hub h ON r.arrivalHubId = h.id " +
            "WHERE r.departureHubId = :departureHubId " +
            "AND r.deletedAt IS NULL " +
            "AND h.deletedAt IS NULL AND h.status = 'ACTIVE'")
    List<Route> findAllActiveRoutesByDepartureHubId(@Param("departureHubId") UUID departureHubId);

    @Modifying
    @Query("UPDATE Route r SET r.deletedAt = CURRENT_TIMESTAMP, r.deletedBy = :userId " +
            "WHERE (r.departureHubId = :hubId OR r.arrivalHubId = :hubId) AND r.deletedAt IS NULL")
    void softDeleteRoutesByHubId(@Param("hubId") UUID hubId, @Param("userId") UUID userId);
}
