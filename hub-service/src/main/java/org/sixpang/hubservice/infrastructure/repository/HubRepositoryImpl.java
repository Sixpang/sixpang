package org.sixpang.hubservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.hubservice.domain.model.entity.Hub;
import org.sixpang.hubservice.domain.repository.HubRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {
    private final HubJpaRepository hubJpaRepository;

    @Override
    public Optional<Hub> findByIdAndDeletedAtIsNull(UUID id){
        return hubJpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Optional<Hub> findByNameAndDeletedAtIsNull(String name){
        return hubJpaRepository.findByNameAndDeletedAtIsNull(name);
    }

    @Override
    public List<Hub> findAllByDeletedAtIsNull(){
        return hubJpaRepository.findAllByDeletedAtIsNull();
    }

    @Override
    public boolean existsByNameAndDeletedAtIsNull(String name){
        return hubJpaRepository.existsByNameAndDeletedAtIsNull(name);
    };

    @Override
    public Page<Hub> findAllByDeletedAtIsNull(Pageable pageable){
        return hubJpaRepository.findAllByDeletedAtIsNull(pageable);
    }

    @Override
    public Hub save(Hub hub){ return hubJpaRepository.save(hub); }
}
