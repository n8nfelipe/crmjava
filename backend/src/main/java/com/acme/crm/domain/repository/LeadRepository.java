package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.Lead;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;

public interface LeadRepository extends JpaRepository<Lead, UUID>, JpaSpecificationExecutor<Lead> {
  long countByStatus(String status);

  long countByConvertedCustomerIsNotNull();
}
