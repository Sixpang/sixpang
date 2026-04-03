package org.sixpang.userservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.userservice.application.dto.UserQueryDto;
import org.sixpang.userservice.application.dto.UserServiceDto;
import org.sixpang.userservice.application.service.UserQueryService;
import org.sixpang.userservice.application.service.UserService;
import org.sixpang.userservice.domain.model.enums.UserRole;
import org.sixpang.userservice.presentation.dto.PageResponseDto;
import org.sixpang.userservice.presentation.dto.UserRequestDto;
import org.sixpang.userservice.presentation.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto.UserDetailResponse>> getUser(
            @PathVariable UUID id
    ) {
        UserQueryDto.UserDetail dto = userQueryService.getUser(id);

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
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<List<UserResponseDto.UserResponse>>>> getUsers(Pageable pageable) {

        Page<UserQueryDto.UserInfo> users = userQueryService.getUsers(pageable);

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
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto.UserUpdateResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequestDto.UpdateUserRequest request
    ) {
        userService.updateUser(
                id,
                UserServiceDto.Update.builder()
                        .name(request.getName())
                        .phone(request.getPhone())
                        .slackId(request.getSlackId())
                        .build()
        );

        UserQueryDto.UserDetail dto = userQueryService.getUser(id);

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
    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequestDto.ChangePasswordRequest request
    ) {
        userService.changePassword(
                id,
                UserServiceDto.ChangePassword.builder()
                        .currentPassword(request.getCurrentPassword())
                        .newPassword(request.getNewPassword())
                        .build()
        );

        return ResponseEntity.ok(ApiResponse.of("비밀번호 변경 성공", null));
    }

    /**회원 상태 변경**/
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponseDto.UserSimpleResponse>> changeStatus(
            @PathVariable UUID id,
            @RequestParam String status
    ) {
        userService.changeStatus(id, status);

        UserQueryDto.UserDetail dto = userQueryService.getUser(id);

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
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {

        UUID currentUserId = UUID.randomUUID();

        userService.deleteUser(userId, currentUserId);

        return ResponseEntity.ok(
                ApiResponse.of("회원 삭제가 완료되었습니다.", null)
        );
    }

    /** 이메일 조회 **/
    @GetMapping("/email")
    public ResponseEntity<ApiResponse<UserQueryDto.AuthUser>> getUserByEmail(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("조회 성공", userQueryService.getUserByEmail(email))
        );
    }
}