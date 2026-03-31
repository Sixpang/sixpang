package org.sixpang.commonserver.config;

import org.sixpang.commonserver.audit.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
