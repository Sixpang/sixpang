package org.sixpang.hubservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.hubservice.domain.model.entity.Hub;
import org.sixpang.hubservice.domain.model.entity.Route;
import org.sixpang.hubservice.domain.repository.RouteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RouteRepositoryImpl implements RouteRepository {
    private final RouteRepository routeRepository;

    @Override
    public Optional<Hub> findByIdAndDeletedAtIsNull(UUID id){
        return routeRepository.findByIdAndDeletedAtIsNull(id);
    };

    @Override
    public Optional<Hub> findByNameAndDeletedAtIsNull(String name){
        return routeRepository.findByNameAndDeletedAtIsNull(name);
    };

    @Override
    public boolean existsByNameAndDeletedAtIsNull(String name){
        return routeRepository.existsByNameAndDeletedAtIsNull(name);
    };

    @Override
    public Page<Hub> findAllByDeletedAtIsNull(Pageable pageable){
        return routeRepository.findAllByDeletedAtIsNull(pageable);
    };

    @Override
    public List<Hub> findAllByDeletedAtIsNull(){
        return routeRepository.findAllByDeletedAtIsNull();
    };

    @Override
    public Route save(Route route){
       return routeRepository.save(route);
    };
}
