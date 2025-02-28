package org.nexters.jaknaesoserver.domain.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;

public record KakaoLoginRequest(@NotBlank String code, @NotBlank String redirectUri) {

  public KakaoLoginCommand toServiceDto() {
    return new KakaoLoginCommand(code, redirectUri);
  }
}
