package org.sixpang.userservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.userservice.application.dto.UserServiceDto;
import org.sixpang.userservice.application.dto.query.UserDetail;
import org.sixpang.userservice.application.dto.query.UserInfo;
import org.sixpang.userservice.application.dto.query.AuthUser;
import org.sixpang.userservice.application.service.UserQueryService;
import org.sixpang.userservice.application.service.UserService;
import org.sixpang.commonserver.enums.UserRole;
import org.sixpang.userservice.domain.model.enums.UserStatus;
import org.sixpang.userservice.presentation.dto.PageResponseDto;
import org.sixpang.userservice.presentation.dto.UserRequestDto;
import org.sixpang.userservice.presentation.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserQueryService userQueryService;

    /**회원가입**/
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto.UserSimpleResponse>> signup(
            @Valid @RequestBody UserRequestDto.SignUpRequest request
    ) {
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

        return ResponseEntity.ok(
                ApiResponse.of("회원가입이 완료되었습니다. 관리자 승인 후 이용 가능합니다.",
                        new UserResponseDto.UserSimpleResponse(
                                userId,
                                request.getEmail(),
                                request.getName(),
                                request.getRole(),
                                "PENDING"
                        )
                )
        );
    }

    /**단건 조회**/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto.UserDetailResponse>> getUser(
            @PathVariable UUID id
    ) {
        UserDetail dto = userQueryService.getUser(id);

        return ResponseEntity.ok(
                ApiResponse.of("회원 상세 조회 성공",
                        new UserResponseDto.UserDetailResponse(
                                dto.getId(),
                                dto.getEmail(),
                                dto.getName(),
                                dto.getPhone(),
                                dto.getSlackId(),
                                dto.getRole().name(),
                                dto.getStatus().name(),
                                dto.getHubId(),
                                dto.getCompanyId()
                        )
                )
        );
    }

    /**목록 조회**/
    @PreAuthorize("hasRole('MASTER')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<List<UserResponseDto.UserResponse>>>> getUsers(Pageable pageable) {

        Page<UserInfo> users = userQueryService.getUsers(pageable);

        List<UserResponseDto.UserResponse> content =
                users.map(dto -> new UserResponseDto.UserResponse(
                        dto.getId(),
                        dto.getEmail(),
                        dto.getName(),
                        dto.getPhone(),
                        dto.getRole().name(),
                        dto.getStatus().name()
                )).getContent();

        PageResponseDto<List<UserResponseDto.UserResponse>> response =
                new PageResponseDto<>(
                        content,
                        new PageResponseDto.Meta(
                                users.getTotalElements(),
                                users.getTotalPages(),
                                users.getNumber()
                        )
                );

        return ResponseEntity.ok(
                ApiResponse.of("회원 목록 조회 성공", response)
        );
    }

    /**회원 정보 수정***/
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto.UserUpdateResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequestDto.UpdateUserRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = UUID.fromString(authentication.getName());
        UserRole role = UserRole.valueOf(authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));

        userService.updateUser(
                id,
                currentUserId,
                role,
                UserServiceDto.Update.builder()
                        .name(request.getName())
                        .phone(request.getPhone())
                        .slackId(request.getSlackId())
                        .build()
        );

        UserDetail dto = userQueryService.getUser(id);

        return ResponseEntity.ok(
                ApiResponse.of("회원 정보 수정이 완료되었습니다.",
                        new UserResponseDto.UserUpdateResponse(
                                dto.getId(),
                                dto.getName(),
                                dto.getPhone(),
                                dto.getRole().name(),
                                dto.getHubId(),
                                dto.getCompanyId()
                        )
                )
        );
    }

    /**비밀번호 변경**/
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequestDto.ChangePasswordRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = UUID.fromString(authentication.getName());
        UserRole role = UserRole.valueOf(authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));

        userService.changePassword(
                id,
                currentUserId,
                role,
                UserServiceDto.ChangePassword.builder()
                        .currentPassword(request.getCurrentPassword())
                        .newPassword(request.getNewPassword())
                        .build()
        );

        return ResponseEntity.ok(ApiResponse.of("비밀번호 변경 성공", null));
    }

    /**회원 상태 변경**/
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponseDto.UserSimpleResponse>> changeStatus(
            @PathVariable UUID id,
            @RequestParam UserStatus status
    ) {
        userService.changeStatus(id, status);

        UserDetail dto = userQueryService.getUser(id);

        return ResponseEntity.ok(
                ApiResponse.of("회원 상태가 성공적으로 변경되었습니다.",
                        new UserResponseDto.UserSimpleResponse(
                                dto.getId(),
                                dto.getEmail(),
                                dto.getName(),
                                dto.getRole().name(),
                                dto.getStatus().name()
                        )
                )
        );
    }

    /**회원 삭제**/
    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = UUID.fromString(authentication.getName());
        UserRole role = UserRole.valueOf(authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));

        userService.deleteUser(userId, currentUserId, role);

        return ResponseEntity.ok(
                ApiResponse.of("회원 삭제가 완료되었습니다.", null)
        );
    }

    /** 이메일 조회 **/
    @GetMapping("/email")
    public ResponseEntity<ApiResponse<AuthUser>> getUserByEmail(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("조회 성공", userQueryService.getUserByEmail(email))
        );
    }
}