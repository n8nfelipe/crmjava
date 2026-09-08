package com.acme.crm.config;

import java.util.Optional;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class AuditingConfig {
  @Bean
  AuditorAware<String> auditorAware() {
    return () ->
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(a -> a.isAuthenticated() && !"anonymousUser".equals(a.getPrincipal()))
            .map(a -> a.getName())
            .or(() -> Optional.of("system"));
  }
}
