package com.acme.crm.application.dto;

import com.acme.crm.domain.model.RoleName;
import jakarta.validation.constraints.*;
import java.util.*;

public final class UserDtos {
  private UserDtos() {}

  public record Create(
      @NotBlank String name,
      @Email @NotBlank String email,
      @Size(min = 10, max = 100) String password,
      @NotEmpty Set<RoleName> roles) {}

  public record Update(@NotBlank String name, @NotEmpty Set<RoleName> roles, boolean enabled) {}

  public record View(UUID id, String name, String email, boolean enabled, Set<RoleName> roles) {}
}
