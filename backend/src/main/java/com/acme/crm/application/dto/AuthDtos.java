package com.acme.crm.application.dto;

import jakarta.validation.constraints.*;
import java.util.*;

public final class AuthDtos {
  private AuthDtos() {}

  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}

  public record RefreshRequest(@NotBlank String refreshToken) {}

  public record LogoutRequest(@NotBlank String refreshToken) {}

  public record ForgotPasswordRequest(@Email @NotBlank String email) {}

  public record ResetPasswordRequest(
      @NotBlank String token, @Size(min = 10, max = 100) String newPassword) {}

  public record UserView(UUID id, String name, String email, Set<String> roles) {}

  public record TokenResponse(
      String accessToken, String refreshToken, String tokenType, long expiresIn, UserView user) {}
}
