package org.sixpang.companyservice.presentation;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.companyservice.application.CompanyService;
import org.sixpang.companyservice.application.dto.CompanyResponse;
import org.sixpang.companyservice.application.dto.CreateCompanyRequest;
import org.sixpang.companyservice.application.dto.UpdateCompanyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@RequestBody CreateCompanyRequest request){
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("업체 생성 성공", response));
    }

    @PatchMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(@PathVariable UUID companyId,@RequestBody UpdateCompanyRequest request){
        CompanyResponse response = companyService.updateCompany(companyId,request);
        return ResponseEntity.ok(ApiResponse.of("업체 수정 성공",response));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@PathVariable UUID companyId){
        CompanyResponse response = companyService.getCompany(companyId);
        return ResponseEntity.ok(ApiResponse.of("업체 조회 성공", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> getCompanies(Pageable pageable){
        Page<CompanyResponse> response = companyService.getCompanies(pageable);
        return ResponseEntity.ok(ApiResponse.of("업체 목록 조회 성공",response));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable UUID companyId){
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok(ApiResponse.of("업체 삭제 성공", null));
    }

}
