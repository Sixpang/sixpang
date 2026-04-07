package org.sixpang.commonserver.audit;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.GlobalErrorCode;
import org.sixpang.commonserver.security.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 🔥 인증 없으면 그냥 비움 (회원가입 허용)
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal.getUserId());
        }

        return Optional.empty();
    }
}