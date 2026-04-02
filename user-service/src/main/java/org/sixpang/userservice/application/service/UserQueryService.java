package org.sixpang.userservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.userservice.application.dto.UserQueryDto;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;

    /**단건 조회 (상세)**/
    public UserQueryDto.UserDetail getUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return new UserQueryDto.UserDetail(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getSlackId(),
                user.getRole(),
                user.getStatus(),
                user.getHubId(),
                user.getCompanyId(),
                user.getCreatedAt()
        );
    }

    /**목록 조회**/
    public List<UserQueryDto.UserInfo> getUsers() {

        return userRepository.findAll().stream()
                .map(user -> new UserQueryDto.UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        user.getPhone(),
                        user.getRole(),
                        user.getStatus()
                ))
                .toList();
    }
}