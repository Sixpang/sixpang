package org.sixpang.companyservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "업체", description = "업체(Company) 관련 API")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
            summary = "업체 생성",
            description = """
                    업체를 생성합니다.
                    마스터 관리자 또는 허브 관리자만 생성 가능합니다.
                    허브 관리자는 본인이 속한 허브에만 업체를 생성할 수 있습니다.
                    """
    )

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @RequestBody CreateCompanyRequest request,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        CompanyResponse response = companyService.createCompany(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("업체 생성 성공", response));
    }

    @Operation(
            summary = "업체 수정",
            description = """
                    업체 정보를 수정합니다.
                    마스터 관리자 또는 해당 허브 관리자가 수정할 수 있습니다.
                    """
    )

    @PatchMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable UUID companyId,
            @RequestBody UpdateCompanyRequest request,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        CompanyResponse response = companyService.updateCompany(companyId, request,user);
        return ResponseEntity.ok(ApiResponse.of("업체 수정 성공", response));
    }

    @Operation(
            summary = "업체 상세 조회",
            description = "특정 업체의 상세 정보를 조회합니다."
    )

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@PathVariable UUID companyId) {
        CompanyResponse response = companyService.getCompany(companyId);
        return ResponseEntity.ok(ApiResponse.of("업체 상세 조회 성공", response));
    }

    @Operation(
            summary = "업체 목록 조회",
            description = """
                    업체 목록을 조회합니다.
                    이름, 타입, 허브 ID, 주소 조건으로 검색이 가능합니다.
                    """
    )

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

    @Operation(
            summary = "업체 삭제",
            description = """
                    업체를 삭제합니다. (논리 삭제)
                    마스터 관리자 또는 해당 허브 관리자만 삭제할 수 있습니다.
                    """
    )

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
            @PathVariable UUID companyId,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        companyService.deleteCompany(companyId,user);
        return ResponseEntity.ok(ApiResponse.of("업체 삭제 성공", null));
    }

    @Operation(
            summary = "업체 존재 여부 확인",
            description = "해당 업체 ID가 존재하는지 여부를 반환합니다. (FeignClient 내부 API 용도)"
    )

    @GetMapping("/{companyId}/exists")
    public boolean exists(@PathVariable UUID companyId) {
        return companyService.exists(companyId);
    }
}
