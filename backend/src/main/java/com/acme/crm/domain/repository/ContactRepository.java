package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.Contact;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
  List<Contact> findByCustomerIdOrderByPrimaryContactDescName(UUID customerId);
}
