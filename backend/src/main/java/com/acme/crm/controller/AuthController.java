package com.acme.crm.controller;

import com.acme.crm.application.dto.AuthDtos.*;
import com.acme.crm.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService service;

  @PostMapping("/login")
  TokenResponse login(@Valid @RequestBody LoginRequest r) {
    return service.login(r);
  }

  @PostMapping("/refresh")
  TokenResponse refresh(@Valid @RequestBody RefreshRequest r) {
    return service.refresh(r);
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void logout(@Valid @RequestBody LogoutRequest r) {
    service.logout(r);
  }

  @PostMapping("/forgot-password")
  @ResponseStatus(HttpStatus.ACCEPTED)
  void forgot(@Valid @RequestBody ForgotPasswordRequest r) {
    service.forgot(r);
  }

  @PostMapping("/reset-password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void reset(@Valid @RequestBody ResetPasswordRequest r) {
    service.reset(r);
  }
}
