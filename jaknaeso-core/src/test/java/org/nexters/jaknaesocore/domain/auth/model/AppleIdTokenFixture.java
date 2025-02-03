package org.nexters.jaknaesocore.domain.auth.model;

import static org.junit.jupiter.api.Assertions.*;

import lombok.Builder;

class AppleIdTokenFixture {

  @Builder
  private static AppleIdToken createAppleIdToken(final String idToken) {
    return new AppleIdToken(idToken);
  }
}
