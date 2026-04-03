package org.sixpang.userservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.userservice.domain.model.enums.UserRole;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceDto {

    /**회원가입 DTO**/
    @Getter
    @Builder
    public static class SignUp {

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        private final String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,15}$",
                message = "비밀번호는 8~15자, 대소문자/숫자/특수문자를 포함해야 합니다."
        )
        private final String password;

        @NotBlank(message = "이름은 필수입니다.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
                message = "이름은 2~10자의 한글, 영문, 숫자만 가능합니다."
        )
        private final String name;

        @NotBlank(message = "전화번호는 필수입니다.")
        private final String phone;

        private final UserRole role;

        private final String slackId;
        private final UUID hubId;
        private final UUID companyId;
    }

    /**회원 정보 수정 DTO**/
    @Getter
    @Builder
    public static class Update {

        @NotBlank(message = "이름은 필수입니다.")
        private final String name;

        @NotBlank(message = "전화번호는 필수입니다.")
        private final String phone;

        private final String slackId;
    }

    /**비밀번호 변경 DTO**/
    @Getter
    @Builder
    public static class ChangePassword {

        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        private final String currentPassword;

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,15}$",
                message = "비밀번호는 8~15자, 대소문자/숫자/특수문자를 포함해야 합니다."
        )
        private final String newPassword;
    }
}