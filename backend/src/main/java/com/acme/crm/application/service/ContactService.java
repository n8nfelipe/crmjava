package com.acme.crm.application.service;

import com.acme.crm.application.dto.ContactDtos.*;
import com.acme.crm.domain.exception.NotFoundException;
import com.acme.crm.domain.model.Contact;
import com.acme.crm.domain.repository.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactService {
  private final ContactRepository repo;
  private final CustomerRepository customers;
  private final AuditService audit;

  @Transactional(readOnly = true)
  public List<Response> list(UUID customerId) {
    return repo.findByCustomerIdOrderByPrimaryContactDescName(customerId).stream()
        .map(this::view)
        .toList();
  }

  @Transactional
  public Response create(UUID customerId, Request r) {
    var c = new Contact();
    c.setCustomer(
        customers
            .findById(customerId)
            .orElseThrow(() -> new NotFoundException("Cliente não encontrado")));
    apply(c, r);
    repo.save(c);
    audit.record("Contact", c.getId(), "CREATE", "{}");
    return view(c);
  }

  @Transactional
  public void delete(UUID id) {
    var c = repo.findById(id).orElseThrow(() -> new NotFoundException("Contato não encontrado"));
    repo.delete(c);
    audit.record("Contact", id, "DELETE", "{}");
  }

  private void apply(Contact c, Request r) {
    c.setName(r.name());
    c.setJobTitle(r.jobTitle());
    c.setPhone(r.phone());
    c.setEmail(r.email());
    c.setPrimaryContact(r.primaryContact());
  }

  private Response view(Contact c) {
    return new Response(
        c.getId(),
        c.getCustomer().getId(),
        c.getName(),
        c.getJobTitle(),
        c.getPhone(),
        c.getEmail(),
        c.isPrimaryContact());
  }
}
