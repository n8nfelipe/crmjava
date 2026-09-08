package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends AuditedEntity {
  public enum Type {
    PERSON,
    COMPANY
  }

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Type type;

  @Column(nullable = false)
  private String name;

  @Column(unique = true)
  private String document;

  private String email;
  private String phone;
  private String address;

  @Column(columnDefinition = "text")
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY)
  private User owner;

  @ManyToMany
  @JoinTable(
      name = "customer_tags",
      joinColumns = @JoinColumn(name = "customer_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new HashSet<>();
}
