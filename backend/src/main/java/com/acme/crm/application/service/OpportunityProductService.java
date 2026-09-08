package com.acme.crm.application.service;

import com.acme.crm.application.dto.OpportunityProductDtos.*;
import com.acme.crm.domain.exception.*;
import com.acme.crm.domain.model.OpportunityProduct;
import com.acme.crm.domain.repository.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpportunityProductService {
  private final OpportunityProductRepository lines;
  private final OpportunityRepository opportunities;
  private final ProductRepository products;
  private final AuditService audit;

  @Transactional(readOnly = true)
  public List<Response> list(UUID opportunityId) {
    return lines.findByOpportunityId(opportunityId).stream().map(this::view).toList();
  }

  @Transactional
  public Response upsert(UUID opportunityId, Request r) {
    var opportunity =
        opportunities
            .findById(opportunityId)
            .orElseThrow(() -> new NotFoundException("Oportunidade não encontrada"));
    var product =
        products
            .findById(r.productId())
            .orElseThrow(() -> new NotFoundException("Produto não encontrado"));
    var line =
        lines
            .findByOpportunityIdAndProductId(opportunityId, r.productId())
            .orElseGet(OpportunityProduct::new);
    line.setOpportunity(opportunity);
    line.setProduct(product);
    line.setQuantity(r.quantity());
    line.setUnitPrice(r.unitPrice());
    lines.save(line);
    audit.record(
        "Opportunity",
        opportunityId,
        "PRODUCT_UPSERT",
        "{\"productId\":\"" + product.getId() + "\"}");
    return view(line);
  }

  @Transactional
  public void remove(UUID opportunityId, UUID productId) {
    var line =
        lines
            .findByOpportunityIdAndProductId(opportunityId, productId)
            .orElseThrow(() -> new NotFoundException("Produto não associado"));
    lines.delete(line);
    audit.record(
        "Opportunity", opportunityId, "PRODUCT_REMOVE", "{\"productId\":\"" + productId + "\"}");
  }

  private Response view(OpportunityProduct x) {
    return new Response(
        x.getId(),
        x.getProduct().getId(),
        x.getProduct().getName(),
        x.getQuantity(),
        x.getUnitPrice(),
        x.getUnitPrice().multiply(java.math.BigDecimal.valueOf(x.getQuantity())));
  }
}
