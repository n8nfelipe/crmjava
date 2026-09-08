package com.acme.crm.application.service;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.ProductDtos.*;
import com.acme.crm.domain.exception.NotFoundException;
import com.acme.crm.domain.model.Product;
import com.acme.crm.domain.repository.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository repo;
  private final AuditService audit;

  @Transactional(readOnly = true)
  public PageResponse<Response> list(Pageable p) {
    return PageResponse.from(repo.findAll(p).map(this::view));
  }

  @Transactional
  public Response create(Request r) {
    var x = new Product();
    apply(x, r);
    repo.save(x);
    audit.record("Product", x.getId(), "CREATE", "{}");
    return view(x);
  }

  @Transactional
  public Response update(UUID id, Request r) {
    var x = find(id);
    apply(x, r);
    audit.record("Product", id, "UPDATE", "{}");
    return view(x);
  }

  @Transactional
  public void delete(UUID id) {
    repo.delete(find(id));
    audit.record("Product", id, "DELETE", "{}");
  }

  private Product find(UUID id) {
    return repo.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado"));
  }

  private void apply(Product x, Request r) {
    x.setName(r.name());
    x.setCategory(r.category());
    x.setPrice(r.price());
    x.setActive(r.active());
  }

  private Response view(Product x) {
    return new Response(x.getId(), x.getName(), x.getCategory(), x.getPrice(), x.isActive());
  }
}
