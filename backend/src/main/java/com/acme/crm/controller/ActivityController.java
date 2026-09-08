package com.acme.crm.controller;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.ActivityDtos.*;
import com.acme.crm.application.service.ActivityService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
  private final ActivityService service;

  @GetMapping
  PageResponse<Response> list(@PageableDefault(size = 20, sort = "dueAt") Pageable p) {
    return service.list(p);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Response create(@Valid @RequestBody Request r) {
    return service.create(r);
  }

  @PatchMapping("/{id}/complete")
  Response complete(@PathVariable UUID id) {
    return service.complete(id);
  }
}
