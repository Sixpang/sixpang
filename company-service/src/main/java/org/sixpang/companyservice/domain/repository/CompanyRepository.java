package org.sixpang.companyservice.domain.repository;

import org.sixpang.companyservice.domain.model.Company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {

    Company save(Company company);
    Optional<Company> findByIdAndDeletedAtIsNull(UUID companyId);
    List<Company> findAllByDeletedAtIsNull();
}
