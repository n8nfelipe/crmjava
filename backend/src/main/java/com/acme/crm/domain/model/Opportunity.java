package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "opportunities")
@Getter
@Setter
@NoArgsConstructor
public class Opportunity extends AuditedEntity {
  public enum Priority {
    LOW,
    MEDIUM,
    HIGH
  }

  public enum Status {
    OPEN,
    WON,
    LOST
  }

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  private Lead lead;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private PipelineStage stage;

  @ManyToOne(fetch = FetchType.LAZY)
  private User owner;

  @Column(nullable = false)
  private BigDecimal value = BigDecimal.ZERO;

  private int probability;
  private LocalDate expectedCloseDate;

  @Enumerated(EnumType.STRING)
  private Priority priority = Priority.MEDIUM;

  @Enumerated(EnumType.STRING)
  private Status status = Status.OPEN;

  private String lossReason;
}
