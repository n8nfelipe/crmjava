package com.acme.crm.controller;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.OpportunityDtos.*;
import com.acme.crm.application.service.OpportunityService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('opportunity:write')")
public class OpportunityController {
  private final OpportunityService service;

  @GetMapping("/api/opportunities")
  PageResponse<Response> list(
      @PageableDefault(size = 50, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable p) {
    return service.list(p);
  }

  @PostMapping("/api/opportunities")
  @ResponseStatus(HttpStatus.CREATED)
  Response create(@Valid @RequestBody Request r) {
    return service.create(r);
  }

  @PatchMapping("/api/opportunities/{id}/stage")
  Response move(@PathVariable UUID id, @Valid @RequestBody StageRequest r) {
    return service.move(id, r);
  }

  @PatchMapping("/api/opportunities/{id}/close")
  Response close(@PathVariable UUID id, @Valid @RequestBody CloseRequest r) {
    return service.close(id, r);
  }

  @GetMapping("/api/pipeline")
  PipelineView pipeline() {
    return service.pipeline();
  }
}
