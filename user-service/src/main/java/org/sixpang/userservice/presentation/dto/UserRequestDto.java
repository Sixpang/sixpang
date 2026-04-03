package org.sixpang.userservice.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class UserRequestDto {

    @Getter
    @NoArgsConstructor
    public static class SignUpRequest {

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,15}$",
                message = "비밀번호는 8~15자, 대소문자/숫자/특수문자를 포함해야 합니다."
        )
        private String password;

        @NotBlank(message = "이름은 필수입니다.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
                message = "이름은 2~10자의 한글, 영문, 숫자만 가능합니다."
        )
        private String name;

        @NotBlank(message = "전화번호는 필수입니다.")
        private String phone;

        @NotBlank(message = "권한은 필수입니다.")
        private String role;

        private String slackId;
        private UUID hubId;
        private UUID companyId;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateUserRequest {

        private String name;
        private String phone;
        private String slackId;
    }

    @Getter
    @NoArgsConstructor
    public static class ChangePasswordRequest {

        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        private String currentPassword;

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,15}$",
                message = "비밀번호는 8~15자, 대소문자/숫자/특수문자를 포함해야 합니다."
        )
        private String newPassword;
    }
}