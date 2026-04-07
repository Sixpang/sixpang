package org.sixpang.userservice.domain.repository;

import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    // Command
    User save(User user);

    // Query
    Optional<User> findByIdAndDeletedAtIsNull(UUID id);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, UUID id);

    Page<User> findAllByDeletedAtIsNull(Pageable pageable);

    Page<User> findAllByStatusAndDeletedAtIsNull(UserStatus status, Pageable pageable);

    Page<User> findAllByStatusAndHubIdAndDeletedAtIsNull(
            UserStatus status,
            UUID hubId,
            Pageable pageable
    );
}