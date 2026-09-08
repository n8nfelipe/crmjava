package com.acme.crm.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record AppProperties(Jwt jwt, String corsAllowedOrigins, String frontendUrl) {
  public AppProperties {
    if (corsAllowedOrigins == null || corsAllowedOrigins.isBlank())
      corsAllowedOrigins = "http://localhost:4200";
    if (frontendUrl == null || frontendUrl.isBlank()) frontendUrl = "http://localhost:4200";
  }

  public record Jwt(String secret, Duration accessTtl, Duration refreshTtl) {}
}
