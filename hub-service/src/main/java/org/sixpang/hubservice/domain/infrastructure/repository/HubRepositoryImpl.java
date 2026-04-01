package org.sixpang.hubservice.domain.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.hubservice.domain.model.entity.Hub;
import org.sixpang.hubservice.domain.repository.HubRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {
    private final HubJpaRepository hubJpaRepository;

    @Override
    public Hub save(Hub hub){ return hubJpaRepository.save(hub); }
}
