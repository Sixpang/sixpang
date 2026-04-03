package org.sixpang.companyservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.companyservice.domain.model.Company;
import org.sixpang.companyservice.domain.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepository {

    private final CompanyJpaRepository companyJpaRepository;

    @Override
    public Company save(Company company) {
        return companyJpaRepository.save(company);
    }

    @Override
    public Optional<Company> findByIdAndDeletedAtIsNull(UUID companyId) {
        return companyJpaRepository.findByIdAndDeletedAtIsNull(companyId);
    }

    @Override
    public Page<Company> findAllByDeletedAtIsNull(Pageable pageable) {
        return companyJpaRepository.findAllByDeletedAtIsNull(pageable);
    }
}
