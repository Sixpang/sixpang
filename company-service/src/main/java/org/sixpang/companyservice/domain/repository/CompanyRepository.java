package org.sixpang.companyservice.domain.repository;

import org.sixpang.companyservice.application.dto.CompanySearchRequest;
import org.sixpang.companyservice.application.dto.CreateCompanyRequest;
import org.sixpang.companyservice.domain.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {

    Company save(Company company);
    Optional<Company> findByIdAndDeletedAtIsNull(UUID companyId);
    Boolean existsByNameAndDeletedAtIsNull(String name);
    Page<Company> searchCompanies(CompanySearchRequest request, Pageable pageable);

}
