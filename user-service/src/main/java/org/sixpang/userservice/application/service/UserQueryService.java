package org.sixpang.userservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.userservice.application.dto.UserQueryDto;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.repository.UserRepository;
import org.sixpang.userservice.exception.UserErrorCode;
import org.sixpang.userservice.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;

    /**단건 조회 (상세)**/
    public UserQueryDto.UserDetail getUser(UUID userId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 코드 리뷰: DTO 생성 책임을 DTO로 이동 (from 메서드 사용)
        return UserQueryDto.UserDetail.from(user);
    }

    /**목록 조회**/
    public Page<UserQueryDto.UserInfo> getUsers(Pageable pageable) {

        return userRepository.findAllByDeletedAtIsNull(pageable)
                // 코드 리뷰: 객체 생성 로직을 DTO로 위임
                .map(UserQueryDto.UserInfo::from);
    }

    /** 로그인용 이메일 조회 (AuthService에서 사용) **/
    public UserQueryDto.AuthUser getUserByEmail(String email) {

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 코드 리뷰: DTO 생성 책임을 DTO로 이동
        return UserQueryDto.AuthUser.from(user);
    }
}