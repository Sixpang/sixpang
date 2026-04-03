package org.sixpang.deliveryservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.domain.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryManagerService managerService;
    private final DeliveryRepository deliveryRepository;

}
