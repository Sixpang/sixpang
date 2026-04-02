package org.sixpang.userservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.userservice.application.dto.UserServiceDto;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.repository.UserRepository;
import org.sixpang.userservice.exception.UserErrorCode;
import org.sixpang.userservice.exception.UserException;
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

    /**회원가입**/
    public UUID signUp(UserServiceDto.SignUp dto) {

        // 이메일 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserException(UserErrorCode.EXISTS_EMAIL);
        }

        // 전화번호 중복 체크
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new UserException(UserErrorCode.EXISTS_PHONE);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 유저 생성
        User user = User.create(
                dto.getEmail(),
                encodedPassword,
                dto.getName(),
                dto.getPhone(),
                dto.getRole(),
                dto.getSlackId(),
                dto.getHubId(),
                dto.getCompanyId()
        );

        // 저장
        userRepository.save(user);

        return user.getId();
    }

    /**회원 정보 수정**/
    public void updateUser(UUID userId, UserServiceDto.Update dto) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 전화번호 중복 체크
        if (userRepository.existsByPhoneAndIdNot(dto.getPhone(), userId)) {
            throw new UserException(UserErrorCode.EXISTS_PHONE);
        }

        user.update(
                dto.getName(),
                dto.getPhone(),
                dto.getSlackId()
        );
    }

    public void changePassword(UUID userId, UserServiceDto.ChangePassword dto) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

        user.changePassword(encodedPassword);
    }

    /**회원 상태 변경(승인, 거절)**/
    public void changeStatus(UUID userId, String status) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        switch (status) {
            case "APPROVED":
                user.approve();
                break;
            case "REJECTED":
                user.reject();
                break;
            default:
                throw new UserException(UserErrorCode.INVALID_STATUS);
        }
    }

    /**회원삭제**/
    public void deleteUser(UUID userId, UUID currentUserId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.delete(currentUserId);
    }
}