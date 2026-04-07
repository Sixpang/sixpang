package org.sixpang.deliveryservice.application.service.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.DeliveryDetailResponse;
import org.sixpang.deliveryservice.application.dto.DeliverySearchCondition;
import org.sixpang.deliveryservice.application.dto.DeliveryWithRoutesResponse;
import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.repository.DeliveryRepository;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryQueryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryDetailResponse getDeliveryById(
            UUID deliveryId,
            String role,
            UUID requestUserId,
            UUID requestHubId
    ) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        validateAccessPermission(delivery, role, requestUserId, requestHubId);
        return DeliveryDetailResponse.from(delivery);
    }

    public DeliveryWithRoutesResponse getDeliveryWithRoutes(
            UUID deliveryId,
            String role,
            UUID requestUserId,
            UUID requestHubId
    ) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        validateAccessPermission(delivery, role, requestUserId, requestHubId);
        return DeliveryWithRoutesResponse.from(delivery);
    }

    public Page<DeliveryDetailResponse> searchDeliveries(
            DeliverySearchCondition condition,
            Pageable pageable,
            String role,
            UUID requestUserId,
            UUID requestHubId
    ) {
        Page<Delivery> deliveries = searchByRole(pageable, role, requestUserId, requestHubId);
        return deliveries.map(DeliveryDetailResponse::from);
    }

    private Delivery findDeliveryOrThrow(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(
                        DeliveryErrorCode.DELIVERY_NOT_FOUND));
    }

    private void validateAccessPermission(
            Delivery delivery,
            String role,
            UUID requestUserId,
            UUID requestHubId
    ) {
        switch (role) {
            case "MASTER":
                return;

            case "HUB_MANAGER":
                if (requestHubId == null) {
                    throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
                }
                boolean hasHubAccess = delivery.getDepartureHub().equals(requestHubId)
                        || delivery.getArrivalHub().equals(requestHubId);
                if (!hasHubAccess) {
                    throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
                }
                return;

            case "DELIVERY_MANAGER":
                // 본인이 담당하는 배송인지 확인 (간단히 deliveryManagerId로 확인)
                boolean isAssigned = delivery.getDeliveryRoutes().stream()
                        .anyMatch(route -> route.getHubDeliveryManagerId().equals(requestUserId));
                if (!isAssigned) {
                    throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
                }
                return;

            case "COMPANY_MANAGER":
                if (!delivery.getReceiverId().equals(requestUserId)) {
                    throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
                }
                return;

            default:
                throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
        }
    }

    private Page<Delivery> searchByRole(
            Pageable pageable,
            String role,
            UUID requestUserId,
            UUID requestHubId
    ) {
        return switch (role) {
            case "MASTER" -> deliveryRepository.findAll(pageable);

            case "HUB_MANAGER" -> {
                if (requestHubId == null) {
                    throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
                }
                yield deliveryRepository.findByHubId(requestHubId, pageable);
            }

            case "DELIVERY_MANAGER" -> deliveryRepository.findByDeliveryManagerUserId(requestUserId, pageable);

            case "COMPANY_MANAGER" -> deliveryRepository.findByReceiverIdAndDeletedAtIsNull(requestUserId, pageable);

            default -> throw new CustomException(
                    DeliveryErrorCode.ACCESS_DENIED);
        };
    }
}
