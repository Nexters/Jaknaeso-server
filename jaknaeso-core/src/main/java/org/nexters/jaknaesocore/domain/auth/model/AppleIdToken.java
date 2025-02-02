package org.nexters.jaknaesocore.domain.auth.model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Builder;

public class AppleIdToken {

  private final String idToken;

  @Builder // test only
  private AppleIdToken(final String idToken) {
    this.idToken = idToken;
  }

  public static AppleIdToken of(final String idToken) {
    return new AppleIdToken(idToken);
  }

  public String decodePayload() {
    final String delimiter = "\\.";
    final String[] parts = idToken.split(delimiter);
    final Base64.Decoder decoder = Base64.getUrlDecoder();

    byte[] decodedBytes = decoder.decode(parts[1].getBytes(StandardCharsets.UTF_8));
    String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

    return decodedString;
  }
}
