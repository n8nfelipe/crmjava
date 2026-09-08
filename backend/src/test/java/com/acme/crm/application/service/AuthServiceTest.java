package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.application.dto.AuthDtos.LoginRequest;
import com.acme.crm.config.AppProperties;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import com.acme.crm.security.*;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @Mock private AuthenticationManager authenticationManager;
  @Mock private UserRepository users;
  @Mock private RefreshTokenRepository refreshTokens;
  @Mock private PasswordResetTokenRepository resetTokens;
  @Mock private JwtService jwt;
  @Mock private TokenHashService hashes;
  @Mock private AppProperties props;
  @Mock private AppProperties.Jwt jwtProperties;
  @Mock private PasswordEncoder encoder;
  @Mock private JavaMailSender mail;
  @Mock private Authentication authentication;
  @InjectMocks private AuthService service;

  @Test
  void authenticatesAndIssuesAccessAndRefreshTokens() {
    var id = UUID.randomUUID();
    var principal =
        new UserPrincipal(
            id,
            "Admin",
            "admin@example.com",
            "hash",
            true,
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    var user = new User();
    user.setId(id);
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(principal);
    when(hashes.random()).thenReturn("refresh-token");
    when(hashes.hash("refresh-token")).thenReturn("refresh-hash");
    when(users.getReferenceById(id)).thenReturn(user);
    when(props.jwt()).thenReturn(jwtProperties);
    when(jwtProperties.refreshTtl()).thenReturn(Duration.ofDays(30));
    when(jwt.create(principal)).thenReturn("access-token");
    when(jwt.expiresIn()).thenReturn(900L);

    var response = service.login(new LoginRequest("admin@example.com", "password"));

    assertThat(response.accessToken()).isEqualTo("access-token");
    assertThat(response.refreshToken()).isEqualTo("refresh-token");
    assertThat(response.user().roles()).containsExactly("ADMIN");
    var captor = ArgumentCaptor.forClass(RefreshToken.class);
    verify(refreshTokens).save(captor.capture());
    assertThat(captor.getValue().getTokenHash()).isEqualTo("refresh-hash");
    assertThat(captor.getValue().getUser()).isSameAs(user);
  }
}
