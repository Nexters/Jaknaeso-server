package org.nexters.jaknaesocore.domain.auth.dto;

import org.nexters.jaknaesocore.domain.auth.service.dto.LoginKakaoCommand;

public record LoginKakaoRequest(String accessToken) {

  public LoginKakaoCommand toServiceDto() {
    return new LoginKakaoCommand(accessToken);
  }
}
