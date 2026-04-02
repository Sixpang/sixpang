package org.sixpang.hubservice.application.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.hubservice.domain.model.enums.HubStatus;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HubRequestDto {
    @NotBlank(message = "허브명을 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]+$", message = "허브명에는 특수문자를 사용할 수 없습니다.")
    private String name;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "위도를 입력해주세요.")
    @DecimalMin(value = "-90.0", message = "위도는 -90 미만일 수 없습니다.")
    @DecimalMax(value = "90.0", message = "위도는 90 초과일 수 없습니다.")
    @Digits(integer = 2, fraction = 6, message = "위도는 정수 2자리, 소수 6자리까지 허용됩니다.")
    private BigDecimal latitude;

    @NotBlank(message = "경도를 입력해주세요.")
    @DecimalMin(value = "-180.0", message = "경도는 -180 미만일 수 없습니다.")
    @DecimalMax(value = "180.0", message = "경도는 180 초과일 수 없습니다.")
    @Digits(integer = 3, fraction = 6, message = "경도는 정수 3자리, 소수 6자리까지 허용됩니다.")
    private BigDecimal longitude;

    @NotBlank(message = "허브 운영 상태를 입력해주세요(ACTIVE, SUSPENDED, CLOSED)")
    private HubStatus status;
}
