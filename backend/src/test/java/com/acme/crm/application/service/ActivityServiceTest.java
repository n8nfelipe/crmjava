package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.application.dto.ActivityDtos.Request;
import com.acme.crm.domain.model.Activity;
import com.acme.crm.domain.repository.*;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {
  @Mock private ActivityRepository repo;
  @Mock private CustomerRepository customers;
  @Mock private LeadRepository leads;
  @Mock private OpportunityRepository opportunities;
  @Mock private UserRepository users;
  @Mock private AuditService audit;
  @InjectMocks private ActivityService service;

  @Test
  void createsPendingActivityAndRecordsAudit() {
    var id = UUID.randomUUID();
    var dueAt = Instant.now().plusSeconds(3600);
    var request =
        new Request(Activity.Type.CALL, "Ligar", "Retorno", dueAt, null, null, null, null);
    when(repo.save(any(Activity.class)))
        .thenAnswer(
            invocation -> {
              var activity = invocation.getArgument(0, Activity.class);
              activity.setId(id);
              return activity;
            });

    var response = service.create(request);

    assertThat(response.id()).isEqualTo(id);
    assertThat(response.state()).isEqualTo("PENDING");
    verify(audit).record("Activity", id, "CREATE", "{}");
  }
}
