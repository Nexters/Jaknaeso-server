package org.nexters.jaknaesoserver.domain.auth.dto;

public record TokenResponse(Long memberId, String accessToken, String refreshToken) {

  public static TokenResponse of(Long memberId, String accessToken, String refreshToken) {
    return new TokenResponse(memberId, accessToken, refreshToken);
  }
}
