package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
  @Mock private EntityManager entityManager;
  @Mock private Query query;
  @InjectMocks private ReportService service;

  @Test
  void generatesCsvAndEscapesQuotes() {
    var from = LocalDate.of(2026, 1, 1);
    var to = LocalDate.of(2026, 1, 31);
    when(entityManager.createNativeQuery(anyString())).thenReturn(query);
    when(query.setParameter("from", from)).thenReturn(query);
    when(query.setParameter("to", to)).thenReturn(query);
    when(query.getResultList())
        .thenReturn(
            List.<Object[]>of(
                new Object[] {
                  LocalDate.of(2026, 1, 10),
                  "Projeto \"Alpha\"",
                  "Acme",
                  new BigDecimal("1200.00"),
                  "Ana",
                  "WON"
                }));

    var csv = new String(service.salesCsv(from, to), StandardCharsets.UTF_8);

    assertThat(csv).startsWith("data,oportunidade,cliente,valor,vendedor,status\n");
    assertThat(csv).contains("\"Projeto \"\"Alpha\"\"\"");
    assertThat(csv).contains("\"1200.00\"");
  }
}
