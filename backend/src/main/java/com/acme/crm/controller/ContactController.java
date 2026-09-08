package com.acme.crm.controller;

import com.acme.crm.application.dto.ContactDtos.*;
import com.acme.crm.application.service.ContactService;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContactController {
  private final ContactService service;

  @GetMapping("/customers/{customerId}/contacts")
  List<Response> list(@PathVariable UUID customerId) {
    return service.list(customerId);
  }

  @PostMapping("/customers/{customerId}/contacts")
  @ResponseStatus(HttpStatus.CREATED)
  Response create(@PathVariable UUID customerId, @Valid @RequestBody Request r) {
    return service.create(customerId, r);
  }

  @DeleteMapping("/contacts/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable UUID id) {
    service.delete(id);
  }
}
