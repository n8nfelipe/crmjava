package com.acme.crm.controller;

import com.acme.crm.application.dto.PageResponse;
import com.acme.crm.domain.model.AuditLog;
import com.acme.crm.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {
  private final AuditLogRepository repo;

  @GetMapping
  PageResponse<AuditLog> list(
      @PageableDefault(size = 50, sort = "occurredAt", direction = Sort.Direction.DESC)
          Pageable p) {
    return PageResponse.from(repo.findAll(p));
  }
}
