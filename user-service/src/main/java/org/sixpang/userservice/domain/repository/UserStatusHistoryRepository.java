package org.sixpang.userservice.domain.repository;

import org.sixpang.userservice.domain.model.entity.UserStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserStatusHistoryRepository extends JpaRepository<UserStatusHistory, UUID> {

    List<UserStatusHistory> findByUserIdOrderByCreatedAtDesc(UUID userId);

}