package com.acme.crm.controller;

import com.acme.crm.application.dto.OpportunityProductDtos.*;
import com.acme.crm.application.service.OpportunityProductService;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/opportunities/{opportunityId}/products")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('opportunity:write')")
public class OpportunityProductController {
  private final OpportunityProductService service;

  @GetMapping
  List<Response> list(@PathVariable UUID opportunityId) {
    return service.list(opportunityId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Response upsert(@PathVariable UUID opportunityId, @Valid @RequestBody Request r) {
    return service.upsert(opportunityId, r);
  }

  @DeleteMapping("/{productId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void remove(@PathVariable UUID opportunityId, @PathVariable UUID productId) {
    service.remove(opportunityId, productId);
  }
}
