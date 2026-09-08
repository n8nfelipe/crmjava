package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.application.dto.OpportunityDtos.Request;
import com.acme.crm.domain.model.*;
import com.acme.crm.domain.repository.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {
  @Mock private OpportunityRepository opportunities;
  @Mock private CustomerRepository customers;
  @Mock private LeadRepository leads;
  @Mock private UserRepository users;
  @Mock private PipelineStageRepository stages;
  @Mock private PipelineRepository pipelines;
  @Mock private AuditService audit;
  @InjectMocks private OpportunityService service;

  @Test
  void createsOpportunityUsingStageDefaults() {
    var opportunityId = UUID.randomUUID();
    var stageId = UUID.randomUUID();
    var stage = new PipelineStage();
    stage.setId(stageId);
    stage.setName("Proposta");
    stage.setClosingProbability(60);
    when(stages.findById(stageId)).thenReturn(Optional.of(stage));
    when(opportunities.save(any(Opportunity.class)))
        .thenAnswer(
            invocation -> {
              var opportunity = invocation.getArgument(0, Opportunity.class);
              opportunity.setId(opportunityId);
              return opportunity;
            });

    var response =
        service.create(
            new Request(
                "Contrato anual",
                null,
                null,
                stageId,
                null,
                new BigDecimal("1000.00"),
                null,
                null,
                null));

    assertThat(response.id()).isEqualTo(opportunityId);
    assertThat(response.probability()).isEqualTo(60);
    assertThat(response.priority()).isEqualTo(Opportunity.Priority.MEDIUM);
    verify(audit).record("Opportunity", opportunityId, "CREATE", "{}");
  }
}
