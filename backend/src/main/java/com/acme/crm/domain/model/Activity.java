package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
public class Activity {
  public enum Type {
    TASK,
    CALL,
    MEETING,
    FOLLOW_UP,
    REMINDER
  }

  @Id private UUID id;

  @Enumerated(EnumType.STRING)
  private Type type;

  @Column(nullable = false)
  private String subject;

  @Column(columnDefinition = "text")
  private String description;

  @Column(nullable = false)
  private Instant dueAt;

  private Instant completedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  private Lead lead;

  @ManyToOne(fetch = FetchType.LAZY)
  private Opportunity opportunity;

  @ManyToOne(fetch = FetchType.LAZY)
  private User owner;

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
