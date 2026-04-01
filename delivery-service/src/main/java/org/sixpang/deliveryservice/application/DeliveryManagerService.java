package org.sixpang.deliveryservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.domain.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryManagerService {
    //주문 담당자 생성
    public UUID createDeliveryMangager(UUID user_id){

        return DeliveryRepository.save(deliveryManager).getId();
    }
}
