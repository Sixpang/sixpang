package org.sixpang.commonserver.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

public class AuditorAwareImpl implements AuditorAware<UUID> {


    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.of(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        //나중에 JWT 만들어지면 SecurityContextHolder.getContext().getAuthentication()으로 UUID값 받아와야됨
    }
}
