package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.Customer;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;

public interface CustomerRepository
    extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
  boolean existsByDocumentAndIdNot(String document, UUID id);
}
