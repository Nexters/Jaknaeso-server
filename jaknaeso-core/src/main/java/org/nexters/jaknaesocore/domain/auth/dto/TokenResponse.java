package org.nexters.jaknaesocore.domain.auth.dto;

public record TokenResponse(String accessToken, String refreshToken) {

  public static TokenResponse of(String accessToken, String refreshToken) {
    return new TokenResponse(accessToken, refreshToken);
  }
}
