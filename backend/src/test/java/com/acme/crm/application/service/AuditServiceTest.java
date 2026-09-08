package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.crm.domain.model.AuditLog;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {
  @Mock private EntityManager entityManager;
  @Mock private Authentication authentication;
  @InjectMocks private AuditService service;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void recordsAuthenticatedActor() {
    var entityId = UUID.randomUUID();
    when(authentication.getName()).thenReturn("user@example.com");
    var context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    service.record("Customer", entityId, "CREATE", "{}");

    var captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(entityManager).persist(captor.capture());
    assertThat(captor.getValue())
        .extracting(
            AuditLog::getEntityType,
            AuditLog::getEntityId,
            AuditLog::getAction,
            AuditLog::getActor,
            AuditLog::getChanges)
        .containsExactly("Customer", entityId, "CREATE", "user@example.com", "{}");
  }
}
