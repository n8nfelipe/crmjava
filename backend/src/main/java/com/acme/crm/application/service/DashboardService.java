package com.acme.crm.application.service;

import com.acme.crm.application.dto.DashboardDtos.*;
import com.acme.crm.domain.model.Opportunity;
import com.acme.crm.domain.repository.*;
import jakarta.persistence.EntityManager;
import java.math.*;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {
  private final CustomerRepository customers;
  private final LeadRepository leads;
  private final OpportunityRepository opportunities;
  private final ActivityRepository activities;
  private final EntityManager em;

  @Transactional(readOnly = true)
  public View get() {
    long total = leads.count(), converted = leads.countByConvertedCustomerIsNotNull();
    return new View(
        customers.count(),
        leads.countByStatus("NEW"),
        opportunities.countByStatus(Opportunity.Status.OPEN),
        opportunities.countByStatus(Opportunity.Status.WON),
        opportunities.forecastRevenue(),
        total == 0 ? 0 : converted * 100.0 / total,
        activities.countByCompletedAtIsNullAndDueAtBefore(Instant.now()),
        series());
  }

  @SuppressWarnings("unchecked")
  private List<SeriesPoint> series() {
    List<Object[]> rows =
        em.createNativeQuery(
                "select to_char(date_trunc('month', updated_at),'YYYY-MM') period,"
                    + " coalesce(sum(value),0), count(*) from opportunities where status='WON' and"
                    + " updated_at >= current_date - interval '11 months' group by 1 order by 1")
            .getResultList();
    return rows.stream()
        .map(r -> new SeriesPoint((String) r[0], (BigDecimal) r[1], ((Number) r[2]).longValue()))
        .toList();
  }
}
