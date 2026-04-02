package org.sixpang.companyservice.infrastructure.repository;

import org.sixpang.companyservice.domain.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyJpaRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByIdAndDeletedAtIsNull(UUID companyId);
    List<Company> findAllByDeletedAtIsNull();
}
