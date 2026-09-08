package com.acme.crm.application.service;

import com.acme.crm.domain.model.AuditLog;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {
  private final EntityManager em;

  public void record(String type, UUID id, String action, String changes) {
    var a = new AuditLog();
    a.setEntityType(type);
    a.setEntityId(id);
    a.setAction(action);
    var auth = SecurityContextHolder.getContext().getAuthentication();
    a.setActor(auth == null ? "system" : auth.getName());
    a.setChanges(changes);
    em.persist(a);
  }
}
