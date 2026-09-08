package com.acme.crm.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public final class OpportunityProductDtos {
  private OpportunityProductDtos() {}

  public record Request(
      @NotNull UUID productId,
      @Min(1) int quantity,
      @NotNull @PositiveOrZero BigDecimal unitPrice) {}

  public record Response(
      UUID id,
      UUID productId,
      String productName,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal total) {}
}
