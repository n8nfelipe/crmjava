package com.acme.crm.application.dto;

import com.acme.crm.domain.model.Customer;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public final class CustomerDtos {
  private CustomerDtos() {}

  public record Request(
      @NotNull Customer.Type type,
      @NotBlank @Size(max = 180) String name,
      @Size(max = 20) String document,
      @Email @Size(max = 180) String email,
      @Size(max = 30) String phone,
      @Size(max = 500) String address,
      @Size(max = 5000) String notes,
      UUID ownerId) {}

  public record Response(
      UUID id,
      Customer.Type type,
      String name,
      String document,
      String email,
      String phone,
      String address,
      String notes,
      UUID ownerId,
      String ownerName,
      Instant createdAt,
      Instant updatedAt,
      long version) {}
}
