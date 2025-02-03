package org.nexters.jaknaesoserver.domain.auth.controller.dto;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginWithTokenCommand;

public record KakaoLoginWithTokenRequest(@NotBlank String accessToken) {

  public KakaoLoginWithTokenCommand toServiceDto() {
    return new KakaoLoginWithTokenCommand(accessToken);
  }
}
