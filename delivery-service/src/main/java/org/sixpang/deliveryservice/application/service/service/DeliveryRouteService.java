package org.sixpang.deliveryservice.application.service.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.DeliveryRouteCreateCommand;
import org.sixpang.deliveryservice.application.dto.DeliveryRouteResponse;
import org.sixpang.deliveryservice.application.dto.RouteCalculationResult;
import org.sixpang.deliveryservice.application.service.strategy.RouteCalculationStrategy;
import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.model.entity.DeliveryRoute;
import org.sixpang.deliveryservice.domain.repository.DeliveryRepository;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.sixpang.deliveryservice.exception.DeliveryRouteErrorCode;
import org.sixpang.deliveryservice.infrastructure.repository.DeliveryRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryRouteService {
    private final DeliveryRouteRepository deliveryRouteRepository;
    private final RouteCalculationStrategy routeCalculationStrategy;
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public UUID createDeliveryRoute(UUID deliveryId, Integer sequence, UUID departureHub, UUID arrivalHub, UUID managerId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        RouteCalculationResult result = routeCalculationStrategy.calculate(departureHub, arrivalHub);

        DeliveryRouteCreateCommand command = new DeliveryRouteCreateCommand(
                delivery,
                sequence,
                departureHub,
                arrivalHub,
                UUID.randomUUID(), // HubRouteId
                result.estimatedDistance(),
                result.estimatedTime(),
                managerId
        );

        DeliveryRoute route = DeliveryRoute.createHubRoute(command);
        return deliveryRouteRepository.save(route).getId();
    }

    public List<DeliveryRouteResponse> getRoutesByDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        return deliveryRouteRepository.findAllByDelivery(delivery).stream()
                .map(this::toResponse)
                .toList();
    }

    public DeliveryRouteResponse getRouteDetail(UUID routeId) {
        DeliveryRoute route = deliveryRouteRepository.findById(routeId)
                .orElseThrow(() -> new CustomException(DeliveryRouteErrorCode.ROUTE_NOT_FOUND));
        return toResponse(route);
    }

    private DeliveryRouteResponse toResponse(DeliveryRoute route) {
        return new DeliveryRouteResponse(
                route.getId(),
                route.getHubSequence(),
                route.getDepartureHub(),
                route.getArrivalHub(),
                route.getEstimatedDistance(),
                route.getEstimatedTime(),
                route.getStatus(),
                route.getActualAt()
        );
    }
}
