package org.sixpang.userservice.infrastructure.repository;

import org.sixpang.userservice.domain.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, UUID id);

    Optional<User> findByIdAndDeletedAtIsNull(UUID id);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
}