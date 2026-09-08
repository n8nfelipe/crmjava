package com.acme.crm.application.service;

import com.acme.crm.application.dto.AuthDtos.*;
import com.acme.crm.config.AppProperties;
import com.acme.crm.domain.exception.BusinessException;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import com.acme.crm.security.*;
import java.time.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final UserRepository users;
  private final RefreshTokenRepository refreshTokens;
  private final PasswordResetTokenRepository resetTokens;
  private final JwtService jwt;
  private final TokenHashService hashes;
  private final AppProperties props;
  private final PasswordEncoder encoder;
  private final JavaMailSender mail;

  @Transactional
  public TokenResponse login(LoginRequest r) {
    var auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(r.email(), r.password()));
    return issue((UserPrincipal) auth.getPrincipal());
  }

  @Transactional
  public TokenResponse refresh(RefreshRequest r) {
    var old =
        refreshTokens
            .findByTokenHash(hashes.hash(r.refreshToken()))
            .orElseThrow(() -> new BusinessException("Refresh token inválido"));
    if (!old.isUsable()) throw new BusinessException("Refresh token expirado ou revogado");
    old.setRevokedAt(Instant.now());
    return issue(UserPrincipal.from(old.getUser()));
  }

  @Transactional
  public void logout(LogoutRequest r) {
    refreshTokens
        .findByTokenHash(hashes.hash(r.refreshToken()))
        .filter(RefreshToken::isUsable)
        .ifPresent(t -> t.setRevokedAt(Instant.now()));
  }

  private TokenResponse issue(UserPrincipal p) {
    String raw = hashes.random();
    var token = new RefreshToken();
    token.setUser(users.getReferenceById(p.id()));
    token.setTokenHash(hashes.hash(raw));
    token.setExpiresAt(Instant.now().plus(props.jwt().refreshTtl()));
    refreshTokens.save(token);
    var roles =
        p.authorities().stream()
            .map(a -> a.getAuthority())
            .filter(a -> a.startsWith("ROLE_"))
            .map(a -> a.substring(5))
            .collect(Collectors.toSet());
    return new TokenResponse(
        jwt.create(p),
        raw,
        "Bearer",
        jwt.expiresIn(),
        new UserView(p.id(), p.name(), p.username(), roles));
  }

  @Transactional
  public void forgot(ForgotPasswordRequest r) {
    users
        .findByEmailIgnoreCase(r.email())
        .ifPresent(
            u -> {
              String raw = hashes.random();
              var t = new PasswordResetToken();
              t.setUser(u);
              t.setTokenHash(hashes.hash(raw));
              t.setExpiresAt(Instant.now().plus(Duration.ofMinutes(30)));
              resetTokens.save(t);
              var m = new SimpleMailMessage();
              m.setTo(u.getEmail());
              m.setSubject("Redefinição de senha do CRM");
              m.setText(
                  "Use este link em até 30 minutos: "
                      + props.frontendUrl()
                      + "/reset-password?token="
                      + raw);
              mail.send(m);
            });
  }

  @Transactional
  public void reset(ResetPasswordRequest r) {
    var t =
        resetTokens
            .findByTokenHash(hashes.hash(r.token()))
            .orElseThrow(() -> new BusinessException("Token de redefinição inválido"));
    if (!t.isUsable()) throw new BusinessException("Token de redefinição expirado ou utilizado");
    t.getUser().setPasswordHash(encoder.encode(r.newPassword()));
    t.setUsedAt(Instant.now());
    refreshTokens.revokeAll(t.getUser().getId(), Instant.now());
  }
}
