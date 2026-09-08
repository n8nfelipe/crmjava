package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.Opportunity;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface OpportunityRepository
    extends JpaRepository<Opportunity, UUID>, JpaSpecificationExecutor<Opportunity> {
  List<Opportunity> findByStagePipelineIdOrderByStagePosition(UUID pipelineId);

  long countByStatus(Opportunity.Status status);

  @Query(
      "select coalesce(sum(o.value*o.probability/100),0) from Opportunity o where o.status='OPEN'")
  BigDecimal forecastRevenue();
}
