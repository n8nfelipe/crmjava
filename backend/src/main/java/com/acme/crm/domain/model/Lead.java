package com.acme.crm.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
public class Lead extends AuditedEntity {
  public enum Temperature {
    COLD,
    WARM,
    HOT
  }

  @Column(nullable = false)
  private String name;

  private String email;
  private String phone;
  private String source;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Temperature temperature = Temperature.WARM;

  @Column(nullable = false)
  private String status = "NEW";

  @ManyToOne(fetch = FetchType.LAZY)
  private User owner;

  @ManyToOne(fetch = FetchType.LAZY)
  private Customer convertedCustomer;
}
