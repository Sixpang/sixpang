package org.sixpang.hubservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.hubservice.domain.model.entity.Route;
import org.sixpang.hubservice.domain.repository.RouteRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RouteRepositoryImpl implements RouteRepository {
    private final RouteJpaRepository routeJpaRepository;

    @Override
    public Optional<Route> findByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(UUID departureHubId, UUID arrivalHubId){
        return routeJpaRepository.findByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(departureHubId, arrivalHubId);
    };

    @Override
    public List<Route> findAllActiveRoutes(){
        return routeJpaRepository.findAllActiveRoutes();
    };

    @Override
    public List<Route> findAllActiveRoutesByDepartureHubId(UUID departureHubId){
        return routeJpaRepository.findAllActiveRoutesByDepartureHubId(departureHubId);
    };

    @Override
    public boolean existsByDepartureHubIdAndArrivalHubId(UUID departureHubId, UUID arrivalHubId){
        return routeJpaRepository.existsByDepartureHubIdAndArrivalHubId(departureHubId, arrivalHubId);
    };

    @Override
    public void softDeleteRoutesByHubId(UUID hubId, UUID userId){
        routeJpaRepository.softDeleteRoutesByHubId(hubId, userId);
    };

    @Override
    public Route save(Route route){
        return routeJpaRepository.save(route);
    };
}
