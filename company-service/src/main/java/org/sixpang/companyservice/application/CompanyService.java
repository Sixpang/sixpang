package org.sixpang.companyservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.companyservice.application.dto.CreateCompanyRequest;
import org.sixpang.companyservice.application.dto.UpdateCompanyRequest;
import org.sixpang.companyservice.domain.model.Company;
import org.sixpang.companyservice.domain.model.CompanyType;
import org.sixpang.companyservice.domain.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public Company createCompany(CreateCompanyRequest request){
        Company company = new Company(
                request.name(),
                request.address(),
                request.type(),
                request.HubId()
        );

        return companyRepository.save(company);
    }

    @Transactional
    public Company updateCompany(UUID companyId, UpdateCompanyRequest request){
        Company company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다."));

        company.update(
                request.name(),
                request.address(),
                request.type(),
                request.hubId()
        );

        return company;
        }

    @Transactional
    public Company deleteCompany(UUID companyId){
        Company company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다."));

        company.delete();

        return company;
    }

    @Transactional(readOnly = true)
    public Company getCompany(UUID companyId){
        return companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new RuntimeException(("업체를 찾을 수 없습니다.")));
    }

    @Transactional(readOnly = true)
    public List<Company> getCompanies(){
        return companyRepository.findAllByDeletedAtIsNull();

    }
}
