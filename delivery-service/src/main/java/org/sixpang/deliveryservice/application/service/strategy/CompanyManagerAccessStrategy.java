package org.sixpang.deliveryservice.application.service.strategy;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyManagerAccessStrategy implements DeliveryAccessStrategy {
    @Override
    public boolean supports(String role) {
        return "COMPANY_MANAGER".equals(role);
    }

    @Override
    public void validateAccess(Delivery delivery, UUID requestUserId, UUID requestHubId) {
        // 업체 관리자는 본인 업체의 주문에 대한 배송만 조회 가능
        // receiverId가 본인 업체 소속인지 확인 필요 (실제로는 FeignClient로 확인)
        if (!delivery.getReceiverId().equals(requestUserId)) {
            throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
        }
    }
}
