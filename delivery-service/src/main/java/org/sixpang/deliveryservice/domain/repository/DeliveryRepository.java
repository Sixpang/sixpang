package org.sixpang.deliveryservice.domain.repository;

import feign.Param;
import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    Page<Delivery> findByDepartureHub(UUID departureHub, Pageable pageable);

    Page<Delivery> findByArrivalHub(UUID arrivalHub, Pageable pageable);

    Page<Delivery> findByReceiverId(UUID receiverId, Pageable pageable);

    Page<Delivery> findByStatus(DeliveryStatus status, Pageable pageable);

    //배송담당자별 조회
    Page<Delivery> findByDeliveryManagerId(UUID deliveryManagerId, Pageable pageable);

    boolean existsByOrderId(UUID orderId);

    Optional<Delivery> findByOrderId(UUID orderId);

    @Query("SELECT d FROM Delivery d WHERE " +
            "(d.departureHub = :hubId OR d.arrivalHub = :hubId) " +
            "AND d.deletedAt IS NULL")
    Page<Delivery> findByHubId(@Param("hubId") UUID hubId, Pageable pageable);

    @Query("SELECT d FROM Delivery d " +
            "JOIN d.deliveryRoutes r " +
            "WHERE r.hubDeliveryManagerId = :userId AND d.deletedAt IS NULL")
    Page<Delivery> findByDeliveryManagerUserId(@Param("userId") UUID userId, Pageable pageable);

    Page<Delivery> findByReceiverIdAndDeletedAtIsNull(UUID receiverId, Pageable pageable);
}
