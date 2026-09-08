package com.acme.crm.controller;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.ProductDtos.*;
import com.acme.crm.application.service.ProductService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService service;

  @GetMapping
  PageResponse<Response> list(@PageableDefault(size = 20, sort = "name") Pageable p) {
    return service.list(p);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  Response create(@Valid @RequestBody Request r) {
    return service.create(r);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  Response update(@PathVariable UUID id, @Valid @RequestBody Request r) {
    return service.update(id, r);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  void delete(@PathVariable UUID id) {
    service.delete(id);
  }
}
