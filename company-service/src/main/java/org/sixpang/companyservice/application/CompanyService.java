package org.sixpang.companyservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.companyservice.application.dto.CompanyResponse;
import org.sixpang.companyservice.application.dto.CompanySearchRequest;
import org.sixpang.companyservice.application.dto.CreateCompanyRequest;
import org.sixpang.companyservice.application.dto.UpdateCompanyRequest;
import org.sixpang.companyservice.domain.model.Company;
import org.sixpang.companyservice.domain.repository.CompanyRepository;
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
    public CompanyResponse createCompany(CreateCompanyRequest request) {
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
    public CompanyResponse updateCompany(UUID companyId, UpdateCompanyRequest request) {
        Company company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다."));

        company.update(
                request.name(),
                request.address(),
                request.type(),
                request.hubId()
        );

        return CompanyResponse.from(company);
    }

    @Transactional
    public void deleteCompany(UUID companyId) {
        Company company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다."));

        company.delete();
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompany(UUID companyId) {
        Company company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new RuntimeException(("업체를 찾을 수 없습니다.")));

        return CompanyResponse.from(company);
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> getCompanies(CompanySearchRequest request, Pageable pageable) {
        return companyRepository.searchCompanies(request,pageable)
                .map(company -> CompanyResponse.from(company));
    }
}
