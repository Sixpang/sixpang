package org.sixpang.hubservice.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.hubservice.domain.model.entity.Hub;
import org.sixpang.hubservice.domain.repository.HubRepository;
import org.sixpang.hubservice.exception.HubErrorCode;
import org.sixpang.hubservice.exception.RouteErrorCode;

import java.util.UUID;

@AllArgsConstructor
public class HubValidator {
    private final HubRepository hubRepository;

    public ValidatedHub validateAndGetActiveHubs(UUID departureHubId, UUID arrivalHubId) {

        if (departureHubId.equals(arrivalHubId)) {
            throw new CustomException(RouteErrorCode.SAME_HUB);
        }

        Hub departureHub = hubRepository.findByIdAndDeletedAtIsNull(departureHubId)
                .orElseThrow(() -> new CustomException(HubErrorCode.HUB_NOT_FOUND));

        Hub arrivalHub = hubRepository.findByIdAndDeletedAtIsNull(arrivalHubId)
                .orElseThrow(() -> new CustomException(HubErrorCode.HUB_NOT_FOUND));

        if (!departureHub.isActive() || !arrivalHub.isActive()) {
            throw new CustomException(HubErrorCode.HUB_NOT_ACTIVE);
        }

        return new ValidatedHub(departureHub, arrivalHub);
    }

    @Getter
    @AllArgsConstructor
    public class ValidatedHub {
        private Hub departureHub;
        private Hub arrivalHub;
    }
}
