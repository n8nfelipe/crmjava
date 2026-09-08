package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag {
  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  private String color;

  @PrePersist
  void create() {
    if (id == null) id = UUID.randomUUID();
  }
}
