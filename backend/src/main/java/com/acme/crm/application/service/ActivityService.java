package com.acme.crm.application.service;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.ActivityDtos.*;
import com.acme.crm.domain.exception.*;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityService {
  private final ActivityRepository repo;
  private final CustomerRepository customers;
  private final LeadRepository leads;
  private final OpportunityRepository opportunities;
  private final UserRepository users;
  private final AuditService audit;

  @Transactional(readOnly = true)
  public PageResponse<Response> list(Pageable p) {
    return PageResponse.from(repo.findAll(p).map(this::view));
  }

  @Transactional
  public Response create(Request r) {
    var a = new Activity();
    a.setType(r.type());
    a.setSubject(r.subject());
    a.setDescription(r.description());
    a.setDueAt(r.dueAt());
    a.setCustomer(get(customers, r.customerId(), "Cliente"));
    a.setLead(get(leads, r.leadId(), "Lead"));
    a.setOpportunity(get(opportunities, r.opportunityId(), "Oportunidade"));
    a.setOwner(get(users, r.ownerId(), "Responsável"));
    repo.save(a);
    audit.record("Activity", a.getId(), "CREATE", "{}");
    return view(a);
  }

  @Transactional
  public Response complete(UUID id) {
    var a = repo.findById(id).orElseThrow(() -> new NotFoundException("Atividade não encontrada"));
    if (a.getCompletedAt() == null) a.setCompletedAt(Instant.now());
    audit.record("Activity", id, "COMPLETE", "{}");
    return view(a);
  }

  private <T> T get(
      org.springframework.data.jpa.repository.JpaRepository<T, UUID> r, UUID id, String n) {
    return id == null
        ? null
        : r.findById(id).orElseThrow(() -> new NotFoundException(n + " não encontrado"));
  }

  private Response view(Activity a) {
    String state =
        a.getCompletedAt() != null
            ? "COMPLETED"
            : a.getDueAt().isBefore(Instant.now()) ? "OVERDUE" : "PENDING";
    return new Response(
        a.getId(),
        a.getType(),
        a.getSubject(),
        a.getDescription(),
        a.getDueAt(),
        a.getCompletedAt(),
        a.getCustomer() == null ? null : a.getCustomer().getId(),
        a.getLead() == null ? null : a.getLead().getId(),
        a.getOpportunity() == null ? null : a.getOpportunity().getId(),
        a.getOwner() == null ? null : a.getOwner().getId(),
        state);
  }
}
