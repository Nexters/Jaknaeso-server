package org.nexters.jaknaesocore.domain.auth.dto;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;

public record KakaoLoginRequest(@NotBlank String code) {

  public KakaoLoginCommand toServiceDto() {
    return new KakaoLoginCommand(code);
  }
}
