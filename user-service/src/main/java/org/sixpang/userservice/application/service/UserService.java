package org.sixpang.userservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.userservice.application.dto.UserServiceDto;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.repository.UserRepository;
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
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 전화번호 중복 체크
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다.");
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        user.update(
                dto.getName(),
                dto.getPhone(),
                dto.getSlackId()
        );
    }

    public void changePassword(UUID userId, UserServiceDto.ChangePassword dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

        user.changePassword(encodedPassword);
    }

    /**회원 승인**/
    public void approveUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        user.approve();
    }

    /**회원 거절**/
    public void rejectUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        user.reject();
    }

    /**회원 삭제 (소프트 삭제)**/
    public void deleteUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        userRepository.delete(user);
    }
}