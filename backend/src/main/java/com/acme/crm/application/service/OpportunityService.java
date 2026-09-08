package com.acme.crm.application.service;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.OpportunityDtos.*;
import com.acme.crm.domain.exception.*;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpportunityService {
  private final OpportunityRepository opportunities;
  private final CustomerRepository customers;
  private final LeadRepository leads;
  private final UserRepository users;
  private final PipelineStageRepository stages;
  private final PipelineRepository pipelines;
  private final AuditService audit;

  @Transactional(readOnly = true)
  public PageResponse<Response> list(Pageable p) {
    return PageResponse.from(opportunities.findAll(p).map(this::view));
  }

  @Transactional
  public Response create(Request r) {
    var o = new Opportunity();
    o.setName(r.name());
    o.setCustomer(get(customers, r.customerId(), "Cliente"));
    o.setLead(get(leads, r.leadId(), "Lead"));
    o.setOwner(get(users, r.ownerId(), "Responsável"));
    o.setStage(
        stages
            .findById(r.stageId())
            .orElseThrow(() -> new NotFoundException("Etapa não encontrada")));
    o.setValue(r.value());
    o.setProbability(
        r.probability() == null ? o.getStage().getClosingProbability() : r.probability());
    o.setExpectedCloseDate(r.expectedCloseDate());
    o.setPriority(r.priority() == null ? Opportunity.Priority.MEDIUM : r.priority());
    opportunities.save(o);
    audit.record("Opportunity", o.getId(), "CREATE", "{}");
    return view(o);
  }

  @Transactional
  public Response move(UUID id, StageRequest r) {
    var o = find(id);
    if (o.getStatus() != Opportunity.Status.OPEN)
      throw new BusinessException("Oportunidade encerrada não pode ser movida");
    o.setStage(
        stages
            .findById(r.stageId())
            .orElseThrow(() -> new NotFoundException("Etapa não encontrada")));
    o.setProbability(o.getStage().getClosingProbability());
    audit.record("Opportunity", id, "STAGE_CHANGE", "{\"stageId\":\"" + r.stageId() + "\"}");
    return view(o);
  }

  @Transactional
  public Response close(UUID id, CloseRequest r) {
    if (r.status() == Opportunity.Status.OPEN)
      throw new BusinessException("Use WON ou LOST para encerrar");
    if (r.status() == Opportunity.Status.LOST
        && (r.lossReason() == null || r.lossReason().isBlank()))
      throw new BusinessException("Motivo da perda é obrigatório");
    var o = find(id);
    o.setStatus(r.status());
    o.setLossReason(r.status() == Opportunity.Status.LOST ? r.lossReason() : null);
    if (r.status() == Opportunity.Status.WON) o.setProbability(100);
    audit.record("Opportunity", id, r.status().name(), "{}");
    return view(o);
  }

  @Transactional(readOnly = true)
  public PipelineView pipeline() {
    var p =
        pipelines
            .findFirstByActiveTrue()
            .orElseThrow(() -> new NotFoundException("Pipeline ativo não encontrado"));
    var byStage =
        opportunities.findByStagePipelineIdOrderByStagePosition(p.getId()).stream()
            .collect(Collectors.groupingBy(o -> o.getStage().getId()));
    return new PipelineView(
        p.getId(),
        p.getName(),
        p.getStages().stream()
            .map(
                s ->
                    new StageView(
                        s.getId(),
                        s.getName(),
                        s.getPosition(),
                        s.getClosingProbability(),
                        byStage.getOrDefault(s.getId(), List.of()).stream()
                            .map(this::view)
                            .toList()))
            .toList());
  }

  private Opportunity find(UUID id) {
    return opportunities
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Oportunidade não encontrada"));
  }

  private <T> T get(
      org.springframework.data.jpa.repository.JpaRepository<T, UUID> r, UUID id, String label) {
    return id == null
        ? null
        : r.findById(id).orElseThrow(() -> new NotFoundException(label + " não encontrado"));
  }

  private Response view(Opportunity o) {
    return new Response(
        o.getId(),
        o.getName(),
        o.getCustomer() == null ? null : o.getCustomer().getId(),
        o.getCustomer() == null ? null : o.getCustomer().getName(),
        o.getLead() == null ? null : o.getLead().getId(),
        o.getStage().getId(),
        o.getStage().getName(),
        o.getOwner() == null ? null : o.getOwner().getId(),
        o.getOwner() == null ? null : o.getOwner().getName(),
        o.getValue(),
        o.getProbability(),
        o.getExpectedCloseDate(),
        o.getPriority(),
        o.getStatus(),
        o.getLossReason());
  }
}
