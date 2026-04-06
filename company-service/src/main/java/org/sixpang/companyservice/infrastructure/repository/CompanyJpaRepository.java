package org.sixpang.companyservice.infrastructure.repository;

import org.sixpang.companyservice.domain.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface CompanyJpaRepository extends JpaRepository<Company, UUID>, CompanyJpaRepositoryCustom {

    Optional<Company> findByIdAndDeletedAtIsNull(UUID companyId);
    Boolean existsByNameAndDeletedAtIsNull(String name);
}
