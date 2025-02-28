package org.nexters.jaknaesoserver.domain.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginWithTokenCommand;

public record KakaoLoginWithTokenRequest(@NotBlank String accessToken) {

  public KakaoLoginWithTokenCommand toServiceDto() {
    return new KakaoLoginWithTokenCommand(accessToken);
  }
}
