package org.sixpang.companyservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.companyservice.application.dto.*;
import org.sixpang.companyservice.domain.model.Company;
import org.sixpang.companyservice.domain.repository.CompanyRepository;
import org.sixpang.companyservice.infrastructure.exception.CompanyErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request, UserPrincipal user) {

        validateCompanyManageRole(user);

        boolean exists = companyRepository.existsByNameAndDeletedAtIsNull(request.name());
        if(exists){
            throw new CustomException(CompanyErrorCode.COMPANY_ALREADY_EXISTS);
        }
        Company company = new Company(
                request.name(),
                request.address(),
                request.type(),
                request.hubId()
        );
        Company savedCompany = companyRepository.save(company);

        return CompanyResponse.from(savedCompany);
    }

    @Transactional
    public CompanyResponse updateCompany(UUID companyId, UpdateCompanyRequest request, UserPrincipal user) {
        Company company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new CustomException(CompanyErrorCode.COMPANY_NOT_FOUND));

        validateCompanyManageRole(user);

        company.update(
                request.name(),
                request.address(),
                request.type(),
                request.hubId()
        );

        return CompanyResponse.from(company);
    }

    @Transactional
    public void deleteCompany(UUID companyId ,UserPrincipal user) {

        Company company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new CustomException(CompanyErrorCode.COMPANY_NOT_FOUND));

        validateCompanyManageRole(user);

        company.delete();
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompany(UUID companyId) {
        Company company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new CustomException(CompanyErrorCode.COMPANY_NOT_FOUND));

        return CompanyResponse.from(company);
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> getCompanies(CompanySearchRequest request, Pageable pageable) {
        return companyRepository.searchCompanies(request,pageable)
                .map(company -> CompanyResponse.from(company));
    }

    @Transactional(readOnly = true)
    public boolean exists(UUID companyId) {
        return companyRepository.findByIdAndDeletedAtIsNull(companyId).isPresent();
    }

    private void validateCompanyManageRole(UserPrincipal user) {
        if (user == null || user.getRole() == null) {
            throw new CustomException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
        }

        String role = user.getRole();

        if ("MASTER".equals(role) || "HUB_MANAGER".equals(role)) {
            return;
        }

        throw new CustomException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
    }
}

