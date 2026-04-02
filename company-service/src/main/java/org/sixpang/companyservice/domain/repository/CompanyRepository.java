package org.sixpang.companyservice.domain.repository;

import org.sixpang.companyservice.domain.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {

    Company save(Company company);
    Optional<Company> findByIdAndDeletedAtIsNull(UUID companyId);
    Page<Company> findAllByDeletedAtIsNull(Pageable pageable);
}
