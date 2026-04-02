package org.sixpang.deliveryservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.domain.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryManagerService managerService;
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public void createDelivery(DeliveryRequestDto request){
        DeliveryAssignmentResult result = managerService.assignManagers(request.getArrivalHub());

    }
}
