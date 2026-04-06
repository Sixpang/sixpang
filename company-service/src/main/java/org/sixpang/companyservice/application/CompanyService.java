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
    private final UserClient userClient;

    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request, UserPrincipal user) {
        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateCreatePermission(userInfo, request.hubId());

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

        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateUpdatePermission(userInfo, company);

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

        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateDeletePermission(userInfo, company);

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

    private UserPermissionInfo getUserPermissionInfo(UserPrincipal user) {
        if (user == null || user.getUserId() == null) {
            throw new CustomException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
        }

        return userClient.getUserPermissionInfo(user.getUserId());
    }

    /**
     * 생성 권한
     * - MASTER 가능
     * - HUB_MANAGER 가능 (본인 hub만)
     * - COMPANY_MANAGER 불가
     * - DRIVER_MANAGER 불가
     */
    private void validateCreatePermission(UserPermissionInfo userInfo, UUID targetHubId) {
        String role = userInfo.role();

        if ("MASTER".equals(role)) {
            return;
        }

        if ("HUB_MANAGER".equals(role)) {
            if (userInfo.hubId() != null && userInfo.hubId().equals(targetHubId)) {
                return;
            }
        }

        throw new CustomException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
    }

    /**
     * 수정 권한
     * - MASTER 가능
     * - HUB_MANAGER 가능 (본인 hub 업체만)
     * - COMPANY_MANAGER 가능 (본인 업체만)
     * - DRIVER_MANAGER 불가
     */
    private void validateUpdatePermission(UserPermissionInfo userInfo, Company company) {
        String role = userInfo.role();

        if ("MASTER".equals(role)) {
            return;
        }

        if ("HUB_MANAGER".equals(role)) {
            if (userInfo.hubId() != null && userInfo.hubId().equals(company.getHubId())) {
                return;
            }
            throw new CustomException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
        }

        if ("COMPANY_MANAGER".equals(role)) {
            if (userInfo.companyId() != null && userInfo.companyId().equals(company.getId())) {
                return;
            }
        }

        throw new CustomException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
    }

    /**
     * 삭제 권한
     * - MASTER 가능
     * - HUB_MANAGER 가능 (본인 hub 업체만)
     * - COMPANY_MANAGER 불가
     * - DRIVER_MANAGER 불가
     */
    private void validateDeletePermission(UserPermissionInfo userInfo, Company company) {
        String role = userInfo.role();

        if ("MASTER".equals(role)) {
            return;
        }

        if ("HUB_MANAGER".equals(role)) {
            if (userInfo.hubId() != null && userInfo.hubId().equals(company.getHubId())) {
                return;
            }
        }

        throw new CustomException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
    }
}

