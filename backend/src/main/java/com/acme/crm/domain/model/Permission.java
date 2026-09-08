package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
public class Permission {
  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private String name;
}
