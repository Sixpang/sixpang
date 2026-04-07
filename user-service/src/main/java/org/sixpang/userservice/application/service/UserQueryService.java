package org.sixpang.userservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.userservice.application.dto.query.UserDetail;
import org.sixpang.userservice.application.dto.query.UserInfo;
import org.sixpang.userservice.application.dto.query.AuthUser;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.model.entity.UserStatusHistory;
import org.sixpang.userservice.domain.model.enums.UserStatus;
import org.sixpang.userservice.domain.repository.UserRepository;
import org.sixpang.userservice.domain.repository.UserStatusHistoryRepository;
import org.sixpang.userservice.exception.UserErrorCode;
import org.sixpang.userservice.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserStatusHistoryRepository userStatusHistoryRepository;

    /**단건 조회 (상세)**/
    public UserDetail getUser(UUID userId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 코드 리뷰: DTO 생성 책임을 DTO로 이동 (from 메서드 사용)
        return UserDetail.from(user);
    }

    /**목록 조회**/
    public Page<UserInfo> getUsers(Pageable pageable) {

        return userRepository.findAllByDeletedAtIsNull(pageable)
                // 코드 리뷰: 객체 생성 로직을 DTO로 위임
                .map(UserInfo::from);
    }

    /**로그인용 이메일 조회 (AuthService에서 사용)**/
    public AuthUser getUserByEmail(String email) {

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 코드 리뷰: DTO 생성 책임을 DTO로 이동
        return AuthUser.from(user);
    }

    /**회원 상태 대기자 조회**/
    public Page<UserInfo> getUsersByStatus(UserStatus status, Pageable pageable) {
        return userRepository
                .findAllByStatusAndDeletedAtIsNull(status, pageable)
                .map(UserInfo::from);
    }

    /**회원 상태 변경 이력 조회**/
    public List<UserStatusHistory> getStatusHistory(UUID userId) {
        return userStatusHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**허브관리자는 해당 허브로 들어온 회원가입 요청만 승인가능 **/
    public Page<UserInfo> getUsersByStatusAndHubId(UserStatus status, UUID hubId, Pageable pageable) {

        return userRepository
                .findAllByStatusAndHubIdAndDeletedAtIsNull(status, hubId, pageable)
                .map(UserInfo::from);
    }
}