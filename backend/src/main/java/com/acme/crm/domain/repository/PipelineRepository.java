package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.Pipeline;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface PipelineRepository extends JpaRepository<Pipeline, UUID> {
  @EntityGraph(attributePaths = "stages")
  Optional<Pipeline> findFirstByActiveTrue();
}
