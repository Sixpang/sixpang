package org.sixpang.userservice.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.sixpang.userservice.application.dto.UserQueryDto;
import org.sixpang.userservice.application.dto.UserServiceDto;
import org.sixpang.userservice.application.service.UserQueryService;
import org.sixpang.userservice.application.service.UserService;
import org.sixpang.userservice.domain.model.enums.UserRole;
import org.sixpang.userservice.presentation.dto.UserRequestDto;
import org.sixpang.userservice.presentation.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserQueryService userQueryService;

    /**회원가입**/
    @PostMapping
    public ResponseEntity<UUID> signup(
            @RequestBody UserRequestDto.SignUpRequest request
    ) {
        // RequestDto → ServiceDto 변환
        UUID userId = userService.signUp(
                UserServiceDto.SignUp.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .name(request.getName())
                        .phone(request.getPhone())
                        .role(UserRole.valueOf(request.getRole()))
                        .slackId(request.getSlackId())
                        .hubId(request.getHubId())
                        .companyId(request.getCompanyId())
                        .build()
        );

        return ResponseEntity.ok(userId);
    }

    /**단건 조회**/
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto.UserDetailResponse> getUser(
            @PathVariable UUID id
    ) {
        UserQueryDto.UserDetail dto = userQueryService.getUser(id);

        // QueryDto → ResponseDto 변환
        return ResponseEntity.ok(
                new UserResponseDto.UserDetailResponse(
                        dto.getId(),
                        dto.getEmail(),
                        dto.getName(),
                        dto.getPhone(),
                        dto.getRole().name(),
                        dto.getSlackId(),
                        dto.getHubId(),
                        dto.getCompanyId(),
                        dto.getStatus().name()
                )
        );
    }

    /**목록 조회**/
    @GetMapping
    public ResponseEntity<Page<UserResponseDto.UserResponse>> getUsers(Pageable pageable) {

        Page<UserQueryDto.UserInfo> users = userQueryService.getUsers(pageable);

        return ResponseEntity.ok(
                users.map(dto -> new UserResponseDto.UserResponse(
                        dto.getId(),
                        dto.getEmail(),
                        dto.getName(),
                        dto.getStatus().name()
                ))
        );
    }

    /**회원 정보 수정**/
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequestDto.UpdateUserRequest request
    ) {
        userService.updateUser(
                id,
                UserServiceDto.Update.builder()
                        .name(request.getName())
                        .phone(request.getPhone())
                        .slackId(request.getSlackId())
                        .build()
        );

        return ResponseEntity.ok().build();
    }

    /**비밀번호 변경**/
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @RequestBody UserRequestDto.ChangePasswordRequest request
    ) {
        userService.changePassword(
                id,
                UserServiceDto.ChangePassword.builder()
                        .currentPassword(request.getCurrentPassword())
                        .newPassword(request.getNewPassword())
                        .build()
        );

        return ResponseEntity.ok().build();
    }

    /**회원 승인**/
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveUser(@PathVariable UUID id) {
        userService.approveUser(id);
        return ResponseEntity.ok().build();
    }

    /**회원 거절**/
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> rejectUser(@PathVariable UUID id) {
        userService.rejectUser(id);
        return ResponseEntity.ok().build();
    }
}