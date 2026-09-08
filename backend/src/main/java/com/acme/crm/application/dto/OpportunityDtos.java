package com.acme.crm.application.dto;

import com.acme.crm.domain.model.Opportunity;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public final class OpportunityDtos {
  private OpportunityDtos() {}

  public record Request(
      @NotBlank String name,
      UUID customerId,
      UUID leadId,
      @NotNull UUID stageId,
      UUID ownerId,
      @NotNull @PositiveOrZero BigDecimal value,
      @Min(0) @Max(100) Integer probability,
      LocalDate expectedCloseDate,
      Opportunity.Priority priority) {}

  public record Response(
      UUID id,
      String name,
      UUID customerId,
      String customerName,
      UUID leadId,
      UUID stageId,
      String stageName,
      UUID ownerId,
      String ownerName,
      BigDecimal value,
      int probability,
      LocalDate expectedCloseDate,
      Opportunity.Priority priority,
      Opportunity.Status status,
      String lossReason) {}

  public record StageRequest(@NotNull UUID stageId) {}

  public record CloseRequest(
      @NotNull Opportunity.Status status, @Size(max = 500) String lossReason) {}

  public record StageView(
      UUID id, String name, int position, int closingProbability, List<Response> opportunities) {}

  public record PipelineView(UUID id, String name, List<StageView> stages) {}
}
