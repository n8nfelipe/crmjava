package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Entity
@Table(name = "pipelines")
@Getter
@Setter
@NoArgsConstructor
public class Pipeline {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  private boolean active = true;

  @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("position")
  private List<PipelineStage> stages = new ArrayList<>();
}
