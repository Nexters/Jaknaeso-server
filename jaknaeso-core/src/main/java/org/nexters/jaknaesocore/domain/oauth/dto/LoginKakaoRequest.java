package org.nexters.jaknaesocore.domain.oauth.dto;

public record LoginKakaoRequest(
    String accessToken
) {

  public LoginKakaoCommand toServiceDto() {
    return new LoginKakaoCommand(accessToken);
  }
}
