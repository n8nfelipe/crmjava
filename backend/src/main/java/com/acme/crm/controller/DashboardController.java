package com.acme.crm.controller;

import com.acme.crm.application.dto.DashboardDtos.View;
import com.acme.crm.application.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
  private final DashboardService service;

  @GetMapping
  View get() {
    return service.get();
  }
}
