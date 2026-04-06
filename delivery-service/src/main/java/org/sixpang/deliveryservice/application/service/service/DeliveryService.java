package org.sixpang.deliveryservice.application.service.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.dto.OrderCreatedEvent;
import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryStatus;
import org.sixpang.deliveryservice.domain.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteService routeService;
    private final DeliveryManagerService managerService;

    @Transactional
    public void createDeliveryFromOrder(OrderCreatedEvent event) {
        // 1. 멱등성 체크: 이미 해당 주문의 배송이 존재하는지 확인
        if (deliveryRepository.existsByOrderId(event.orderId())) {
            log.warn("이미 처리된 주문입니다. orderId: {}", event.orderId());
            return;
        }

        // 2. 경로 및 담당자 배정 (기구현 로직 활용)
        var routes = routeService.createDeliveryRoute(event.arrivalHub());
        var manager = managerService.assignmana(routes);

        // 3. 배송 엔티티 저장
        Delivery delivery = Delivery.builder()
                .orderId(event.orderId())
                .agent(manager)
                .routes(routes)
                .status(DeliveryStatus.CREATED)
                .build();

        deliveryRepository.save(delivery);
    }
}
