package org.sixpang.userservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByIdAndDeletedAtIsNull(UUID id) {
        return userJpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Optional<User> findByEmailAndDeletedAtIsNull(String email) {
        return userJpaRepository.findByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userJpaRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByPhoneAndIdNot(String phone, UUID id) {
        return userJpaRepository.existsByPhoneAndIdNot(phone, id);
    }

    @Override
    public Page<User> findAllByDeletedAtIsNull(Pageable pageable) {
        return userJpaRepository.findAllByDeletedAtIsNull(pageable);
    }
}