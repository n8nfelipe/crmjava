package com.acme.crm.controller;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.CustomerDtos.*;
import com.acme.crm.application.service.CustomerService;
import com.acme.crm.domain.model.Customer;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
  private final CustomerService service;

  @GetMapping
  @PreAuthorize("hasAuthority('customer:read')")
  PageResponse<Response> list(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Customer.Type type,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable p) {
    return service.list(search, type, p);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('customer:read')")
  Response get(@PathVariable UUID id) {
    return service.get(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('customer:write')")
  Response create(@Valid @RequestBody Request r) {
    return service.create(r);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('customer:write')")
  Response update(@PathVariable UUID id, @Valid @RequestBody Request r) {
    return service.update(id, r);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAuthority('customer:write')")
  void delete(@PathVariable UUID id) {
    service.delete(id);
  }
}
