package org.sixpang.hubservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.hubservice.application.dto.HubRequestDto;
import org.sixpang.hubservice.application.dto.HubResponseDto;
import org.sixpang.hubservice.application.service.HubService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "허브", description = "허브 관련 API")
public class HubController {
    private final HubService hubService;

    @Operation(
            summary = "허브 등록",
            description = "허브를 등록합니다. <br>" +
                    "마스터 관리자만 접근 가능합니다. <br>" +
                    "허브가 등록될 시 다른 허브가 존재할 경우 이동 경로가 생성됩니다. <br>" +
                    "마스터 관리자 이외의 사용자는 허브를 등록할 수 없습니다."
    )
    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/hubs")
    public ResponseEntity<ApiResponse<HubResponseDto>> register(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid HubRequestDto requestDto
    ){
        HubResponseDto responseDto = hubService.register(userPrincipal.getUserId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(HttpStatus.CREATED, "허브가 성공적으로 생성되었습니다.", responseDto));
    }

    @Operation(
            summary = "허브 목록 조회",
            description = "전체 허브 목록을 조회합니다. <br>" +
                    "마스터 관리자만 접근 가능합니다."
    )
    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/hubs")
    public ResponseEntity<ApiResponse<PageResponse<HubResponseDto>>> getHubList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        PageResponse<HubResponseDto> pageResponse = hubService.getAllHubInfo(pageable);
        return ResponseEntity.ok(ApiResponse.of("허브 목록이 조회되었습니다.", pageResponse));
    }

    @Operation(
            summary = "허브 상세 정보 조회",
            description = "허브의 상세 정보를 조회합니다. <br>" +
                    "로그인한 모든 사용자가 접근 가능합니다. <br>" +
                    "허브가 등록될 시 다른 허브가 존재할 경우 이동 경로가 생성됩니다."
    )
    @GetMapping("/hubs/{id}")
    public ResponseEntity<ApiResponse<HubResponseDto>> getHubInfo(
            @PathVariable UUID id
    ){
        HubResponseDto responseDto = hubService.getHubInfo(id);
        return ResponseEntity.ok(ApiResponse.of("허브 상세 정보가 조회되었습니다.", responseDto));
    }

    @Operation(
            summary = "허브 정보 수정",
            description = "허브 정보를 수정합니다. <br>" +
                    "마스터 관리자만 접근 가능합니다. <br>" +
                    "허브의 상태 정보가 수정될 시 다른 허브가 존재할 경우 이동 경로가 생성(허브 상태 SUSPENDED/CLOSED -> APPROVED로 변경) " +
                    "혹은 삭제(허브 상태 APPROVED -> SUSPENDED/CLOSED로 변경)됩니다. <br>" +
                    "마스터 관리자 이외의 사용자는 허브를 수정할 수 없습니다."
    )
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/hubs/{id}")
    public ResponseEntity<ApiResponse<HubResponseDto>> updateHubInfo(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid HubRequestDto requestDto
    ){
        HubResponseDto responseDto = hubService.updateHubInfo(id, userPrincipal.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.of("허브 정보가 수정되었습니다.", responseDto));
    }

    @Operation(
            summary = "허브 삭제",
            description = "허브를 삭제합니다. <br>" +
                    "마스터 관리자만 접근 가능합니다. <br>" +
                    "허브는 논리적 삭제 처리됩니다. <br>" +
                    "허브가 삭제될 시 관련된 모든 경로가 삭제됩니다. <br>" +
                    "마스터 관리자 이외의 사용자는 허브를 삭제할 수 없습니다."
    )
    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/hubs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHub(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        hubService.deleteHub(id, userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponse.of("허브가 삭제되었습니다.", null));
    }

    @Operation(
            summary = "허브 존재 여부 확인",
            description = "허브 존재 여부를 확인합니다. <br>" +
                    "로그인한 모든 사용자가 접근 가능합니다."
    )
    @GetMapping("/hubs/{id}/exists")
    public boolean exists(@PathVariable UUID id) {
        return hubService.exists(id);
    }
}
