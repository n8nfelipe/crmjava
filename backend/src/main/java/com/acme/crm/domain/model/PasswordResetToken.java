package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {
  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private User user;

  @Column(name = "token_hash", nullable = false, unique = true, length = 64)
  private String tokenHash;

  @Column(nullable = false)
  private Instant expiresAt;

  private Instant usedAt;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void create() {
    if (id == null) id = UUID.randomUUID();
    createdAt = Instant.now();
  }

  public boolean isUsable() {
    return usedAt == null && expiresAt.isAfter(Instant.now());
  }
}
