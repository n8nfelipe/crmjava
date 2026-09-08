package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
public class Contact {
  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Customer customer;

  @Column(nullable = false)
  private String name;

  private String jobTitle;
  private String phone;
  private String email;
  private boolean primaryContact;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  void create() {
    if (id == null) id = UUID.randomUUID();
    createdAt = updatedAt = Instant.now();
  }

  @PreUpdate
  void update() {
    updatedAt = Instant.now();
  }
}
