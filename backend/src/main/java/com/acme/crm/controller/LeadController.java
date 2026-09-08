package com.acme.crm.controller;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.LeadDtos.*;
import com.acme.crm.application.service.LeadService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('lead:write')")
public class LeadController {
  private final LeadService service;

  @GetMapping
  PageResponse<Response> list(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String status,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable page) {
    return service.list(search, status, page);
  }

  @PostMapping
  Response create(@Valid @RequestBody Request r) {
    return service.create(r);
  }

  @PatchMapping("/{id}/status")
  Response status(@PathVariable UUID id, @Valid @RequestBody StatusRequest r) {
    return service.status(id, r);
  }

  @PostMapping("/{id}/convert")
  ConversionResponse convert(@PathVariable UUID id, @Valid @RequestBody ConvertRequest r) {
    return service.convert(id, r);
  }
}
