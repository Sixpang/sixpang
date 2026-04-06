package org.sixpang.userservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.userservice.application.dto.UserServiceDto;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.commonserver.enums.UserRole;
import org.sixpang.userservice.domain.model.entity.UserStatusHistory;
import org.sixpang.userservice.domain.model.enums.UserStatus;
import org.sixpang.userservice.domain.repository.UserRepository;
import org.sixpang.userservice.domain.repository.UserStatusHistoryRepository;
import org.sixpang.userservice.exception.UserErrorCode;
import org.sixpang.userservice.exception.UserException;
import org.sixpang.userservice.infrastructure.client.CompanyServiceClient;
import org.sixpang.userservice.infrastructure.client.HubServiceClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

//사용자 쓰기 관련 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserStatusHistoryRepository userStatusHistoryRepository;
    private final HubServiceClient hubServiceClient;
    private final CompanyServiceClient companyServiceClient;


    /**회원 가입**/
    public UUID signUp(UserServiceDto.SignUp dto) {

        //코드 리뷰:중복 검사 로직을 메서드 로 분리 하여 가독성 개선
        validateDuplicateUser(dto);

        //허브id 나 업체id 둘중에 하나는 입력해야함
        validateRoleTarget(dto);

        // TODO: 업체, 허브 서비스 연동 후 활성화
        // 허브 존재 여부 검증(허브가 존재하지 않으면 예외처리)
        /*
        if (dto.getHubId() != null) {
            boolean exists = hubServiceClient.exists(dto.getHubId());
            System.out.println("허브 존재 여부: " + exists);

            if (!exists) {
                throw new UserException(UserErrorCode.INVALID_HUB);
            }
        }
        */


        // 업체 존재 여부 검증 (업체가 존재하지 않으면 예외처리)
         /* if (dto.getCompanyId() != null) {
            boolean exists = companyServiceClient.exists(dto.getCompanyId());

            if (!exists) {
                throw new UserException(UserErrorCode.INVALID_COMPANY);
            }
        }*/

        // 비밀 번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 유저 생성
        // 코드 리뷰:DTO가 Entity 생성 책임을 갖도록 위임
        User user = dto.toEntity(encodedPassword);

        // 저장
        userRepository.save(user);

        return user.getId();
    }

    /**회원 정보 수정**/
    public void updateUser(UUID userId, UUID currentUserId, UserRole role, UserServiceDto.Update dto) {

        validateAccess(userId, currentUserId, role);

        User user = findUser(userId);

        // 전화번호 중복 체크 (값 있을 때만)
        if (dto.getPhone() != null &&
                userRepository.existsByPhoneAndIdNot(dto.getPhone(), userId)) {
            throw new UserException(UserErrorCode.EXISTS_PHONE);
        }

        user.update(
                dto.getName(),
                dto.getPhone(),
                dto.getSlackId()
        );
    }

    /**비밀번호 변경 **/
    public void changePassword(UUID userId, UUID currentUserId, UserRole role, UserServiceDto.ChangePassword dto) {

        validateAccess(userId, currentUserId, role);

        User user = findUser(userId);

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

        user.changePassword(encodedPassword);
    }

    /**회원 상태 변경(승인, 거절)**/
    // 코드 리뷰: 하드 코딩 문자열 제거 → UserStatus enum 사용
    // 코드 리뷰: 나중에 enum 관련 조건문을 쓰지 않아도 되는 추상 메서드 구현 방식 추후 설명 해주실 예정
    public void changeStatus(UUID userId, UserStatus status) {

        User user = findUser(userId);

        if (status.isApproved()) {
            user.approve();
        } else if (status.isRejected()) {
            user.reject();
        } else {
            throw new UserException(UserErrorCode.INVALID_STATUS);
        }

        // 상태 변경 이력 저장 추가
        userStatusHistoryRepository.save(
                new UserStatusHistory(userId, status, null)
        );
    }

    /**회원삭제**/
    public void deleteUser(UUID userId, UUID currentUserId, UserRole role) {

        validateAccess(userId, currentUserId, role);

        User user = findUser(userId);

        user.delete(currentUserId);
    }


    /** 공통 조회 메서드 **/

    //코드 리뷰: User 조회 공통 메서드로 관리
    private User findUser(UUID userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
    // 이메일 중복 체크
    private boolean isAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }
    // 전화 번호 중복 체크
    private boolean isAlreadyExistsPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    //코드 리뷰:중복 검사 로직을 한 곳으로 모아 가독성 과 재 사용성 확보
    private void validateDuplicateUser(UserServiceDto.SignUp dto) {
        // 이메일 중복 체크 (코드 리뷰: 예외 처리 조건이 명확 하게 드러나도록 분기문 유지)
        if (isAlreadyExists(dto.getEmail())) {
            throw new UserException(UserErrorCode.EXISTS_EMAIL);
        }
        // 전화 번호 중복 체크 (코드 리뷰:예외 처리 조건이 명확 하게 드러나도록 분기문 유지)
        if (isAlreadyExistsPhone(dto.getPhone())) {
            throw new UserException(UserErrorCode.EXISTS_PHONE);
        }
    }

    //권한 체크 메서드
    private void validateAccess(UUID targetUserId, UUID currentUserId, UserRole role) {
        if (!targetUserId.equals(currentUserId) && role != UserRole.MASTER) {
            throw new UserException(UserErrorCode.FORBIDDEN);
        }
    }

    private void validateRoleTarget(UserServiceDto.SignUp dto) {

        // TODO: 현재는 입력값 검증만 수행
        // - 추후 Feign 연동 후 실제 허브/업체 존재 여부 검증 추가 예정

        boolean hasHub = dto.getHubId() != null;
        boolean hasCompany = dto.getCompanyId() != null;

        // 둘 다 없으면 에러
        if (!hasHub && !hasCompany) {
            throw new UserException(UserErrorCode.INVALID_AFFILIATION_REQUIRED);
        }

        // 둘 다 있으면 에러
        if (hasHub && hasCompany) {
            throw new UserException(UserErrorCode.INVALID_AFFILIATION_DUPLICATE);
        }
    }
}