package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "opportunity_products")
@Getter
@Setter
@NoArgsConstructor
public class OpportunityProduct {
  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Opportunity opportunity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Product product;

  private int quantity;

  @Column(nullable = false)
  private BigDecimal unitPrice;

  @PrePersist
  void create() {
    if (id == null) id = UUID.randomUUID();
  }
}
