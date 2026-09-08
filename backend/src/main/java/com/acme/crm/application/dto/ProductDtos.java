package com.acme.crm.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public final class ProductDtos {
  private ProductDtos() {}

  public record Request(
      @NotBlank String name,
      String category,
      @NotNull @PositiveOrZero BigDecimal price,
      boolean active) {}

  public record Response(UUID id, String name, String category, BigDecimal price, boolean active) {}
}
