package com.acme.crm.application.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

public final class ContactDtos {
  private ContactDtos() {}

  public record Request(
      @NotBlank String name,
      String jobTitle,
      String phone,
      @Email String email,
      boolean primaryContact) {}

  public record Response(
      UUID id,
      UUID customerId,
      String name,
      String jobTitle,
      String phone,
      String email,
      boolean primaryContact) {}
}
