package org.nexters.jaknaesocore.domain.auth.dto;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public record KakaoLoginRequest(@NotBlank String accessToken) {

  public KakaoLoginCommand toServiceDto() {
    return new KakaoLoginCommand(accessToken);
  }
}
