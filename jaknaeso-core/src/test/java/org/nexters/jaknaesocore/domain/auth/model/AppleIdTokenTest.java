package org.nexters.jaknaesocore.domain.auth.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AppleIdTokenTest {

  @Nested
  @DisplayName("of 메소드는 ")
  class of {

    @Test
    @DisplayName("객체를 생성한다.")
    void shouldCreate() {
      final String token = "token";
      then(AppleIdToken.of(token)).extracting("idToken").isEqualTo(token);
    }
  }

  @Nested
  @DisplayName("decodePayload 메소드는 ")
  class decodePayload {

    @Test
    @DisplayName("Apple Auth Id Token을 해독한다.")
    void shouldDecodeAppleIdToken() {
      final AppleIdToken idToken = createAppleIdToken();

      then(idToken.decodePayload())
          .isEqualTo(
              "{\"sub\":\"001234567890123456789012\",\"at_hash\":\"RV_vFJfqat0F2aYtuPRSeg\",\"aud\":\"com.example.appline.web\",\"auth_time\":1605371559,\"iss\":\"https://id.apple.com\",\"exp\":1605375159,\"iat\":1605371559,\"nonce\":\"794795883054469436\",\"email\":\"test@example.com\",\"email_verified\":true}");
    }
  }

  private AppleIdToken createAppleIdToken() {
    return AppleIdTokenFixture.builder()
        .idToken(
            "eyJraWQiOiJBSURPRkZDTzJDM05EUVBGQUJDVEFDT1VDU1ZZQUdTR09ZUEJNVU5KS1FEUVFBQUEyTVE2USIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIwMDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIiLCJhdF9oYXNoIjoiUlZfdkZKZnFhdDBGMmFZdHVQUlNlZyIsImF1ZCI6ImNvbS5leGFtcGxlLmFwcGxpbmUud2ViIiwiYXV0aF90aW1lIjoxNjA1MzcxNTU5LCJpc3MiOiJodHRwczovL2lkLmFwcGxlLmNvbSIsImV4cCI6MTYwNTM3NTE1OSwiaWF0IjoxNjA1MzcxNTU5LCJub25jZSI6Ijc5NDc5NTg4MzA1NDQ2OTQzNiIsImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlfQ.VkmD8KcTtCmN65DPwhAPoOeRuXmsLqnm1z8pWa_qHG3xD2LBJgj9YOZPUKseOlfrOz5e5JgIR1qPdWiL2QFuyjCQZ0PSG0hV1xtQ_yYbVHeqLaID0AgcV8Hxldg9hFvF_jvM8G_mo0S9-D8gOR4kbQ")
        .build();
  }
}
