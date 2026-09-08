package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.application.dto.LeadDtos.Request;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {
  @Mock private LeadRepository leads;
  @Mock private UserRepository users;
  @Mock private CustomerRepository customers;
  @Mock private OpportunityRepository opportunities;
  @Mock private PipelineStageRepository stages;
  @Mock private AuditService audit;
  @InjectMocks private LeadService service;

  @Test
  void createsLeadAndRecordsAudit() {
    var id = UUID.randomUUID();
    var request =
        new Request(
            "Novo lead",
            "lead@example.com",
            "11999999999",
            "Site",
            Lead.Temperature.HOT,
            "NEW",
            null);
    when(leads.save(any(Lead.class)))
        .thenAnswer(
            invocation -> {
              var lead = invocation.getArgument(0, Lead.class);
              lead.setId(id);
              return lead;
            });

    var response = service.create(request);

    assertThat(response.id()).isEqualTo(id);
    assertThat(response.name()).isEqualTo("Novo lead");
    assertThat(response.temperature()).isEqualTo(Lead.Temperature.HOT);
    verify(audit).record("Lead", id, "CREATE", "{}");
  }
}
