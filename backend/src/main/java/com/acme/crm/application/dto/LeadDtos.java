package com.acme.crm.application.dto;

import com.acme.crm.domain.model.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

public final class LeadDtos {
  private LeadDtos() {}

  public record Request(
      @NotBlank @Size(max = 180) String name,
      @Email String email,
      @Size(max = 30) String phone,
      @Size(max = 80) String source,
      @NotNull Lead.Temperature temperature,
      @NotBlank @Size(max = 50) String status,
      UUID ownerId) {}

  public record Response(
      UUID id,
      String name,
      String email,
      String phone,
      String source,
      Lead.Temperature temperature,
      String status,
      UUID ownerId,
      String ownerName,
      UUID convertedCustomerId,
      Instant createdAt,
      Instant updatedAt) {}

  public record StatusRequest(@NotBlank @Size(max = 50) String status) {}

  public record ConvertRequest(
      @NotNull UUID stageId,
      @NotBlank String opportunityName,
      @PositiveOrZero BigDecimal value,
      LocalDate expectedCloseDate) {}

  public record ConversionResponse(UUID customerId, UUID opportunityId) {}
}
