package com.acme.crm.domain.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "pipeline_stages")
@Getter
@Setter
@NoArgsConstructor
public class PipelineStage {
  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Pipeline pipeline;

  @Column(nullable = false)
  private String name;

  private int position;
  private int closingProbability;
}
