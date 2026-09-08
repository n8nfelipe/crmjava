package com.acme.crm.application.dto;

import com.acme.crm.domain.model.Activity;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public final class ActivityDtos {
  private ActivityDtos() {}

  public record Request(
      @NotNull Activity.Type type,
      @NotBlank String subject,
      String description,
      @NotNull @FutureOrPresent Instant dueAt,
      UUID customerId,
      UUID leadId,
      UUID opportunityId,
      UUID ownerId) {}

  public record Response(
      UUID id,
      Activity.Type type,
      String subject,
      String description,
      Instant dueAt,
      Instant completedAt,
      UUID customerId,
      UUID leadId,
      UUID opportunityId,
      UUID ownerId,
      String state) {}
}
