package org.sixpang.hubservice.presentation;

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
public class HubController {
    private final HubService hubService;

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/hubs")
    public ResponseEntity<ApiResponse<HubResponseDto>> register(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid HubRequestDto requestDto
    ){
        HubResponseDto responseDto = hubService.register(userPrincipal.getUserId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(HttpStatus.CREATED, "허브가 성공적으로 생성되었습니다.", responseDto));
    }

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/hubs")
    public ResponseEntity<ApiResponse<PageResponse<HubResponseDto>>> getHubList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        PageResponse<HubResponseDto> pageResponse = hubService.getAllHubInfo(pageable);
        return ResponseEntity.ok(ApiResponse.of("허브 목록이 조회되었습니다.", pageResponse));
    }

    @GetMapping("/hubs/{id}")
    public ResponseEntity<ApiResponse<HubResponseDto>> getHubInfo(
            @PathVariable UUID id
    ){
        HubResponseDto responseDto = hubService.getHubInfo(id);
        return ResponseEntity.ok(ApiResponse.of("허브 상세 정보가 조회되었습니다.", responseDto));
    }

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

    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/hubs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHub(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        hubService.deleteHub(id, userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponse.of("허브가 삭제되었습니다.", null));
    }

    @GetMapping("/hubs/{id}/exists")
    public boolean exists(@PathVariable UUID id) {
        return hubService.exists(id);
    }
}
