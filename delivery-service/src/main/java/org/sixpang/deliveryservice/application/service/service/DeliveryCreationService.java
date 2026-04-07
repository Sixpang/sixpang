package org.sixpang.deliveryservice.application.service.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.DeliveryCreatedEvent;
import org.sixpang.deliveryservice.application.dto.OrderCreatedEvent;
import org.sixpang.deliveryservice.application.dto.RouteCalculationResult;
import org.sixpang.deliveryservice.application.messaging.DeliveryEventPublisher;
import org.sixpang.deliveryservice.application.service.client.DeliveryHubClient;
import org.sixpang.deliveryservice.application.service.strategy.RouteCalculationStrategy;
import org.sixpang.deliveryservice.domain.model.entity.CompanyDeliveryManager;
import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.model.entity.DeliveryRoute;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryStatus;
import org.sixpang.deliveryservice.domain.repository.DeliveryRepository;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.sixpang.deliveryservice.exception.DeliveryRouteErrorCode;
import org.sixpang.deliveryservice.infrastructure.repository.DeliveryRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryCreationService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteRepository deliveryRouteRepository;
    private final DeliveryManagerService deliveryManagerService;
    private final RouteCalculationStrategy routeCalculationStrategy;
    private final DeliveryHubClient hubClient;
    private final DeliveryEventPublisher eventPublisher;

    @Transactional
    public UUID createDeliveryFromOrder(OrderCreatedEvent event) {
        if (isAlreadyProcessed(event.orderId())) {
            log.info("이미 처리된 주문입니다. orderId={}", event.orderId());
            return findDeliveryIdByOrderId(event.orderId());
        }

        validateHubExists(event.departureHub());
        validateHubExists(event.arrivalHub());

        List<DeliveryRoute> routes = createDeliveryRoutes(
                event.departureHub(),
                event.arrivalHub()
        );

        CompanyDeliveryManager companyManager = assignCompanyManager(event.arrivalHub());

        Delivery delivery = buildDelivery(event, routes.get(0).getHubDeliveryManagerId());

        routes.forEach(delivery::addRoute);

        Delivery savedDelivery = deliveryRepository.save(delivery);

        log.info("배송 생성 완료: deliveryId={}, orderId={}, routeCount={}",
                savedDelivery.getId(), event.orderId(), routes.size());

        //publishDeliveryCreatedEvent(savedDelivery);

        return savedDelivery.getId();
    }

    private void publishDeliveryCreatedEvent(Delivery delivery) {
        DeliveryCreatedEvent event = DeliveryCreatedEvent.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .departureHub(delivery.getDepartureHub())
                .arrivalHub(delivery.getArrivalHub())
                .hubDeliveryManagerId(getFirstHubDeliveryManager(delivery.getId()))
                .status(delivery.getStatus().name())
                .build();

        eventPublisher.publishDeliveryCreated(event);
    }

    private UUID getFirstHubDeliveryManager(UUID deliveryId) {
        return deliveryRouteRepository
                .findByDeliveryIdOrderByHubSequenceAsc(deliveryId)
                .stream()
                .findFirst()
                .map(DeliveryRoute::getHubDeliveryManagerId)
                .orElse(null);
    }

    private boolean isAlreadyProcessed(UUID orderId) {
        return deliveryRepository.existsByOrderId(orderId);
    }

    private UUID findDeliveryIdByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .map(Delivery::getId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
    }

    private void validateHubExists(UUID hubId) {
        try {
            hubClient.checkExists(hubId);
        } catch (FeignException.NotFound e) {
            throw new CustomException(
                    DeliveryErrorCode.HUB_NOT_FOUND);
        }
    }

    private List<DeliveryRoute> createDeliveryRoutes(UUID departureHub, UUID arrivalHub) {
        // 1. 경로 계산
        RouteCalculationResult result = routeCalculationStrategy.calculate(departureHub, arrivalHub);

        if (result.hubPath() == null || result.hubPath().isEmpty()) {
            throw new CustomException(
                    DeliveryRouteErrorCode.DELIVERY_PATH_ERROR);
        }

        List<DeliveryRoute> routes = new ArrayList<>();
        List<UUID> hubPath = result.hubPath();

        for (int i = 0; i < hubPath.size() - 1; i++) {
            UUID currentHub = hubPath.get(i);
            UUID nextHub = hubPath.get(i + 1);

            HubDeliveryManager manager = deliveryManagerService.assignHubManager();

            DeliveryRoute route = DeliveryRoute.builder()
                    .hubSequence(i + 1)
                    .departureHub(currentHub)
                    .arrivalHub(nextHub)
                    .hubRouteId(UUID.randomUUID()) // 실제로는 HubRoute ID
                    .estimatedDistance(result.estimatedDistance())
                    .estimatedTime(result.estimatedTime())
                    .hubDeliveryManagerId(manager.getId())
                    .build();

            routes.add(route);
        }

        return routes;
    }

    private CompanyDeliveryManager assignCompanyManager(UUID arrivalHub) {
        try {
            return deliveryManagerService.assignCompanyManager(arrivalHub);
        } catch (Exception e) {
            throw new CustomException(
                    DeliveryErrorCode.MANAGER_ASSIGNMENT_FAILED);
        }
    }

    private Delivery buildDelivery(OrderCreatedEvent event, UUID managerId) {
        return Delivery.builder()
                .orderId(event.orderId())
                .status(DeliveryStatus.HUB_WAITING)
                .departureHub(event.departureHub())
                .arrivalHub(event.arrivalHub())
                .address(event.address())
                .receiverId(event.receiverId())
                .deliveryManagerId(managerId)
                .build();
    }
}