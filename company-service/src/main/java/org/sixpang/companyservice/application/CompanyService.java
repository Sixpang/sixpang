package org.sixpang.companyservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.companyservice.application.dto.CreateCompanyRequest;
import org.sixpang.companyservice.domain.model.Company;
import org.sixpang.companyservice.domain.model.CompanyType;
import org.sixpang.companyservice.domain.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
