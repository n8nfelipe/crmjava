package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.acme.crm.domain.model.Opportunity;
import com.acme.crm.domain.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
  @Mock private CustomerRepository customers;
  @Mock private LeadRepository leads;
  @Mock private OpportunityRepository opportunities;
  @Mock private ActivityRepository activities;
  @Mock private EntityManager entityManager;
  @Mock private Query query;
  @InjectMocks private DashboardService service;

  @Test
  void aggregatesDashboardMetricsAndMonthlySeries() {
    when(customers.count()).thenReturn(12L);
    when(leads.count()).thenReturn(10L);
    when(leads.countByConvertedCustomerIsNotNull()).thenReturn(4L);
    when(leads.countByStatus("NEW")).thenReturn(3L);
    when(opportunities.countByStatus(Opportunity.Status.OPEN)).thenReturn(5L);
    when(opportunities.countByStatus(Opportunity.Status.WON)).thenReturn(2L);
    when(opportunities.forecastRevenue()).thenReturn(new BigDecimal("1500.00"));
    when(activities.countByCompletedAtIsNullAndDueAtBefore(any())).thenReturn(1L);
    when(entityManager.createNativeQuery(any(String.class))).thenReturn(query);
    when(query.getResultList())
        .thenReturn(List.<Object[]>of(new Object[] {"2026-09", new BigDecimal("900.00"), 2L}));

    var view = service.get();

    assertThat(view.totalCustomers()).isEqualTo(12);
    assertThat(view.conversionRate()).isEqualTo(40.0);
    assertThat(view.performance())
        .singleElement()
        .satisfies(
            point -> {
              assertThat(point.period()).isEqualTo("2026-09");
              assertThat(point.revenue()).isEqualByComparingTo("900.00");
              assertThat(point.won()).isEqualTo(2);
            });
  }
}
