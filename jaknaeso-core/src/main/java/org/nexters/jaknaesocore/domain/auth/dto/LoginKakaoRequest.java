package org.nexters.jaknaesocore.domain.auth.dto;

public record LoginKakaoRequest(String accessToken) {

  public LoginKakaoCommand toServiceDto() {
    return new LoginKakaoCommand(accessToken);
  }
}
