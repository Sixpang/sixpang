package org.sixpang.hubservice.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.hubservice.application.dto.HubRequestDto;
import org.sixpang.hubservice.application.dto.HubResponseDto;
import org.sixpang.hubservice.application.service.HubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HubController {
    private final HubService hubService;

    @PreAuthorize("hasRole('SURVEYEE')")
    @PostMapping("/hubs")
    public ResponseEntity<ApiResponse<HubResponseDto>> register(
            @RequestBody @Valid HubRequestDto requestDto
    ){
        HubResponseDto responseDto = hubService.register(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("허브가 성공적으로 생성되었습니다.", responseDto));
    }

}
