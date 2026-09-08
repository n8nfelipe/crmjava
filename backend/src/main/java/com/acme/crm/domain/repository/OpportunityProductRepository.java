package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.OpportunityProduct;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityProductRepository extends JpaRepository<OpportunityProduct, UUID> {
  List<OpportunityProduct> findByOpportunityId(UUID opportunityId);

  Optional<OpportunityProduct> findByOpportunityIdAndProductId(UUID opportunityId, UUID productId);
}
