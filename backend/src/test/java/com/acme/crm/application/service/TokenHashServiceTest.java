package com.acme.crm.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TokenHashServiceTest {
  private final TokenHashService service = new TokenHashService();

  @Test
  void generatesUniqueOpaqueTokensAndStableHashes() {
    String first = service.random(), second = service.random();
    assertThat(first).hasSizeGreaterThan(50).isNotEqualTo(second);
    assertThat(service.hash(first)).hasSize(64).isEqualTo(service.hash(first));
    assertThat(service.hash(first)).isNotEqualTo(service.hash(second));
  }
}
