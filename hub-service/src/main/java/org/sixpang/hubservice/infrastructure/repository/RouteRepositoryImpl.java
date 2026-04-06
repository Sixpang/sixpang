package org.sixpang.hubservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.hubservice.domain.model.entity.Route;
import org.sixpang.hubservice.domain.repository.RouteRepository;
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
