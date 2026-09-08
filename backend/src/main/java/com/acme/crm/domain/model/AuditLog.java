package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {
  @Id private UUID id;

  @Column(nullable = false)
  private String entityType;

  @Column(nullable = false)
  private UUID entityId;

  @Column(nullable = false)
  private String action;

  @Column(nullable = false)
  private String actor;

  @Column(nullable = false)
  private Instant occurredAt;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private String changes;

  @PrePersist
  void create() {
    if (id == null) id = UUID.randomUUID();
    if (occurredAt == null) occurredAt = Instant.now();
  }
}
