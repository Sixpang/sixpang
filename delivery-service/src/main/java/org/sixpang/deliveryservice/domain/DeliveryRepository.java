package org.sixpang.deliveryservice.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    //허브 조회용:후에 동적쿼리로 개선
    Page<Delivery> findByDepartureHub(UUID departureHub, Pageable pageable);
    Page<Delivery> findByArrivalHub(UUID arrivalHub, Pageable pageable);

    //업체 조회용
    Page<Delivery> findByReceiverId(UUID receiverId, Pageable pageable);

    //상태별 조회
    Page<Delivery> findByStatus(DeliveryStatus status, Pageable pageable);

    //배송담당자별 조회
    Page<Delivery> findByDeliveryManagerId(UUID deliveryManagerId, Pageable pageable);
}
