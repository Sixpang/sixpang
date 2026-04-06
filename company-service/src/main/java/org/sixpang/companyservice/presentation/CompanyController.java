package org.sixpang.companyservice.presentation;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.companyservice.application.CompanyService;
import org.sixpang.companyservice.application.dto.CompanyResponse;
import org.sixpang.companyservice.application.dto.CompanySearchRequest;
import org.sixpang.companyservice.application.dto.CreateCompanyRequest;
import org.sixpang.companyservice.application.dto.UpdateCompanyRequest;
import org.sixpang.companyservice.domain.model.CompanyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @RequestBody CreateCompanyRequest request,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        CompanyResponse response = companyService.createCompany(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("업체 생성 성공", response));
    }

    @PatchMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable UUID companyId,
            @RequestBody UpdateCompanyRequest request,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        CompanyResponse response = companyService.updateCompany(companyId, request,user);
        return ResponseEntity.ok(ApiResponse.of("업체 수정 성공", response));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@PathVariable UUID companyId) {
        CompanyResponse response = companyService.getCompany(companyId);
        return ResponseEntity.ok(ApiResponse.of("업체 상세 조회 성공", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CompanyResponse>>> getCompanies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) CompanyType type,
            @RequestParam(required = false) UUID hubId,
            @RequestParam(required = false) String address,
            Pageable pageable) {
        CompanySearchRequest request = new CompanySearchRequest(name, address,type,hubId);
        Page<CompanyResponse> companies = companyService.getCompanies(request,pageable);
        PageResponse<CompanyResponse> response = PageResponse.from(companies);
        return ResponseEntity.ok(ApiResponse.of("업체 목록 조회 성공", response));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
            @PathVariable UUID companyId,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        companyService.deleteCompany(companyId,user);
        return ResponseEntity.ok(ApiResponse.of("업체 삭제 성공", null));
    }

    @GetMapping("/{companyId}/exists")
    public boolean exists(@PathVariable UUID companyId) {
        return companyService.exists(companyId);
    }
}
