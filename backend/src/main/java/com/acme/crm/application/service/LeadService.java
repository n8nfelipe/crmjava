package com.acme.crm.application.service;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.LeadDtos.*;
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
public class LeadService {
  private final LeadRepository leads;
  private final UserRepository users;
  private final CustomerRepository customers;
  private final OpportunityRepository opportunities;
  private final PipelineStageRepository stages;
  private final AuditService audit;

  @Transactional(readOnly = true)
  public PageResponse<Response> list(String search, String status, Pageable p) {
    Specification<Lead> s =
        (r, q, cb) -> {
          var x = new ArrayList<Predicate>();
          if (search != null && !search.isBlank())
            x.add(cb.like(cb.lower(r.get("name")), "%" + search.toLowerCase() + "%"));
          if (status != null && !status.isBlank()) x.add(cb.equal(r.get("status"), status));
          return cb.and(x.toArray(Predicate[]::new));
        };
    return PageResponse.from(leads.findAll(s, p).map(this::view));
  }

  @Transactional
  public Response create(Request r) {
    var l = new Lead();
    apply(l, r);
    leads.save(l);
    audit.record("Lead", l.getId(), "CREATE", "{}");
    return view(l);
  }

  @Transactional
  public Response status(UUID id, StatusRequest r) {
    var l = find(id);
    l.setStatus(r.status());
    audit.record(
        "Lead", id, "STATUS_CHANGE", "{\"status\":\"" + r.status().replace("\"", "") + "\"}");
    return view(l);
  }

  @Transactional
  public ConversionResponse convert(UUID id, ConvertRequest r) {
    var l = find(id);
    if (l.getConvertedCustomer() != null) throw new BusinessException("Lead já convertido");
    var c = new Customer();
    c.setType(Customer.Type.PERSON);
    c.setName(l.getName());
    c.setEmail(l.getEmail());
    c.setPhone(l.getPhone());
    c.setOwner(l.getOwner());
    customers.save(c);
    l.setConvertedCustomer(c);
    l.setStatus("CONVERTED");
    var o = new Opportunity();
    o.setName(r.opportunityName());
    o.setCustomer(c);
    o.setLead(l);
    o.setOwner(l.getOwner());
    o.setStage(
        stages
            .findById(r.stageId())
            .orElseThrow(() -> new NotFoundException("Etapa não encontrada")));
    o.setValue(r.value());
    o.setProbability(o.getStage().getClosingProbability());
    o.setExpectedCloseDate(r.expectedCloseDate());
    opportunities.save(o);
    audit.record(
        "Lead",
        id,
        "CONVERT",
        "{\"customerId\":\"" + c.getId() + "\",\"opportunityId\":\"" + o.getId() + "\"}");
    return new ConversionResponse(c.getId(), o.getId());
  }

  private void apply(Lead l, Request r) {
    l.setName(r.name());
    l.setEmail(r.email());
    l.setPhone(r.phone());
    l.setSource(r.source());
    l.setTemperature(r.temperature());
    l.setStatus(r.status());
    l.setOwner(
        r.ownerId() == null
            ? null
            : users
                .findById(r.ownerId())
                .orElseThrow(() -> new NotFoundException("Responsável não encontrado")));
  }

  private Lead find(UUID id) {
    return leads.findById(id).orElseThrow(() -> new NotFoundException("Lead não encontrado"));
  }

  private Response view(Lead l) {
    return new Response(
        l.getId(),
        l.getName(),
        l.getEmail(),
        l.getPhone(),
        l.getSource(),
        l.getTemperature(),
        l.getStatus(),
        l.getOwner() == null ? null : l.getOwner().getId(),
        l.getOwner() == null ? null : l.getOwner().getName(),
        l.getConvertedCustomer() == null ? null : l.getConvertedCustomer().getId(),
        l.getCreatedAt(),
        l.getUpdatedAt());
  }
}
