package org.sixpang.routeservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.routeservice.domain.model.Route;
import org.sixpang.routeservice.domain.repository.RouteRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RouteRepositoryImpl implements RouteRepository {
    private final RouteRepository routeRepository;

    @Override
    public Route save(Route route){
       return routeRepository.save(route);
    };
}
