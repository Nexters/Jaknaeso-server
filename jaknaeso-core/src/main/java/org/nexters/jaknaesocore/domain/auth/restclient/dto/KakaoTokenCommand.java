package org.nexters.jaknaesocore.domain.auth.restclient.dto;

public record KakaoTokenCommand(
    String grantType, String clientId, String redirectUri, String code, String clientSecret) {

  public static KakaoTokenCommand of(
      final String clientId,
      final String clientSecret,
      final String redirectUri,
      final String code) {
    return new KakaoTokenCommand("authorization_code", clientId, redirectUri, code, clientSecret);
  }
}
