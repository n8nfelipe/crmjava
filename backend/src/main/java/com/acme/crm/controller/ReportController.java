package com.acme.crm.controller;

import com.acme.crm.application.service.ReportService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('report:read')")
public class ReportController {
  private final ReportService service;

  @GetMapping(value = "/sales", produces = "text/csv")
  ResponseEntity<byte[]> sales(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales.csv")
        .contentType(new MediaType("text", "csv"))
        .body(service.salesCsv(from, to));
  }
}
