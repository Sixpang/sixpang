package org.sixpang.hubservice.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
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

    // TODO: 인증/인가 구현 완료 후 주석 해제
    // @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/hubs")
    public ResponseEntity<ApiResponse<HubResponseDto>> register(
            // TODO: 인증 구현 후 -> AuthenticationPrincipal UUID userId로 적용하기
            @RequestHeader(value = "X-User-Id", defaultValue = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
            @RequestBody @Valid HubRequestDto requestDto
    ){
        HubResponseDto responseDto = hubService.register(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("허브가 성공적으로 생성되었습니다.", responseDto));
    }

    // @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/hubs")
    public ResponseEntity<ApiResponse<Page<HubResponseDto>>> getHubList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Page<HubResponseDto> page = hubService.getAllHubInfo(pageable);
        return ResponseEntity.ok(ApiResponse.of("허브 목록이 조회되었습니다.", page));
    }

    @GetMapping("/hubs/{id}")
    public ResponseEntity<ApiResponse<HubResponseDto>> getHubInfo(
            @PathVariable UUID id
    ){
        HubResponseDto responseDto = hubService.getHubInfo(id);
        return ResponseEntity.ok(ApiResponse.of("허브 상세 정보가 조회되었습니다.", responseDto));
    }

    // @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/hubs/{id}")
    public ResponseEntity<ApiResponse<HubResponseDto>> updateHubInfo(
            @PathVariable UUID id,
            // TODO: 인증 인가 후 변경
            @RequestHeader(value = "X-User-Id", defaultValue = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
            @RequestBody @Valid HubRequestDto requestDto
    ){
        HubResponseDto responseDto = hubService.updateHubInfo(id, userId, requestDto);
        return ResponseEntity.ok(ApiResponse.of("허브 정보가 수정되었습니다.", responseDto));
    }

    // @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/hubs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHub(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", defaultValue = "123e4567-e89b-12d3-a456-426614174000") UUID userId
    ){
        hubService.deleteHub(id, userId);
        return ResponseEntity.ok(ApiResponse.of("허브가 삭제되었습니다.", null));
    }
}
