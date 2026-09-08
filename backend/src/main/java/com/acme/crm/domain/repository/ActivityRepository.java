package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.Activity;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;

public interface ActivityRepository
    extends JpaRepository<Activity, UUID>, JpaSpecificationExecutor<Activity> {
  long countByCompletedAtIsNullAndDueAtBefore(Instant now);
}
