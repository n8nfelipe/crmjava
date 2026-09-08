package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.RefreshToken;
import java.time.Instant;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByTokenHash(String hash);

  @Modifying
  @Query(
      "update RefreshToken t set t.revokedAt=:now where t.user.id=:userId and t.revokedAt is null")
  int revokeAll(@Param("userId") UUID userId, @Param("now") Instant now);
}
