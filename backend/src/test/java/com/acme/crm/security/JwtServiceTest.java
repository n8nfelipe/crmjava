package com.acme.crm.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.acme.crm.config.AppProperties;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
  @Mock private AppProperties properties;
  @Mock private AppProperties.Jwt jwtProperties;
  private JwtService service;

  @BeforeEach
  void setUp() {
    when(properties.jwt()).thenReturn(jwtProperties);
    when(jwtProperties.secret()).thenReturn("test-secret-with-at-least-32-characters");
    when(jwtProperties.accessTtl()).thenReturn(Duration.ofMinutes(15));
    service = new JwtService(properties);
  }

  @Test
  void createsTokenAndReadsItsSubject() {
    var principal =
        new UserPrincipal(
            UUID.randomUUID(),
            "Admin",
            "admin@example.com",
            "hash",
            true,
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

    var token = service.create(principal);

    assertThat(service.subject(token)).isEqualTo("admin@example.com");
    assertThat(service.expiresIn()).isEqualTo(900);
  }
}
