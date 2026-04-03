package org.sixpang.deliveryservice.application.dto;

import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerSearchCondition(
        String slackId,           // User 서비스에서 검색 후 userId 목록으로 변환 필요
        UUID hubId,
        DeliveryManagerType type,
        String name               // 마찬가지로 User 서비스 연동 필요
) {}