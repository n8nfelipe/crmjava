package com.acme.crm.application.service;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.CustomerDtos.*;
import com.acme.crm.domain.exception.*;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import jakarta.persistence.criteria.Predicate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
  private final CustomerRepository customers;
  private final UserRepository users;
  private final CustomerMapper mapper;
  private final AuditService audit;

  @Transactional(readOnly = true)
  public PageResponse<Response> list(String search, Customer.Type type, Pageable pageable) {
    Specification<Customer> spec =
        (root, q, cb) -> {
          var ps = new ArrayList<Predicate>();
          if (search != null && !search.isBlank()) {
            String s = "%" + search.toLowerCase() + "%";
            ps.add(
                cb.or(
                    cb.like(cb.lower(root.get("name")), s),
                    cb.like(cb.lower(root.get("email")), s),
                    cb.like(root.get("document"), "%" + search + "%")));
          }
          if (type != null) ps.add(cb.equal(root.get("type"), type));
          return cb.and(ps.toArray(Predicate[]::new));
        };
    return PageResponse.from(customers.findAll(spec, pageable).map(mapper::toResponse));
  }

  @Transactional(readOnly = true)
  public Response get(UUID id) {
    return mapper.toResponse(find(id));
  }

  @Transactional
  public Response create(Request r) {
    var c = mapper.toEntity(r);
    assignOwner(c, r.ownerId());
    customers.save(c);
    audit.record("Customer", c.getId(), "CREATE", "{\"name\":\"" + safe(c.getName()) + "\"}");
    return mapper.toResponse(c);
  }

  @Transactional
  public Response update(UUID id, Request r) {
    var c = find(id);
    if (r.document() != null && customers.existsByDocumentAndIdNot(r.document(), id))
      throw new BusinessException("CPF/CNPJ já cadastrado");
    mapper.update(r, c);
    assignOwner(c, r.ownerId());
    audit.record("Customer", id, "UPDATE", "{\"name\":\"" + safe(c.getName()) + "\"}");
    return mapper.toResponse(c);
  }

  @Transactional
  public void delete(UUID id) {
    var c = find(id);
    customers.delete(c);
    audit.record("Customer", id, "DELETE", "{}");
  }

  private Customer find(UUID id) {
    return customers
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
  }

  private void assignOwner(Customer c, UUID id) {
    c.setOwner(
        id == null
            ? null
            : users
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Responsável não encontrado")));
  }

  private String safe(String s) {
    return s.replace("\\", "\\\\").replace("\"", "\\\"");
  }
}
