package com.acme.crm.controller;

import com.acme.crm.application.dto.*;
import com.acme.crm.application.dto.UserDtos.*;
import com.acme.crm.application.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
  private final UserService service;

  @GetMapping
  PageResponse<View> list(@PageableDefault(size = 20, sort = "name") Pageable p) {
    return service.list(p);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  View create(@Valid @RequestBody Create r) {
    return service.create(r);
  }

  @PutMapping("/{id}")
  View update(@PathVariable UUID id, @Valid @RequestBody Update r) {
    return service.update(id, r);
  }
}
