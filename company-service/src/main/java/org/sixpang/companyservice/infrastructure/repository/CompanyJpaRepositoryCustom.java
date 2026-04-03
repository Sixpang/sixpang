package org.sixpang.companyservice.infrastructure.repository;

import org.sixpang.companyservice.application.dto.CompanySearchRequest;
import org.sixpang.companyservice.domain.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepositoryCustom {
    Page<Company> searchCompanies(CompanySearchRequest request, Pageable pageable);
}
