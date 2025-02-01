package org.nexters.jaknaesoserver.domain.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import org.nexters.jaknaesocore.domain.auth.service.dto.AppleLoginCommand;

public record AppleLoginRequest(
    @NotBlank(message = "애플 로그인이 유효하지 않습니다. 다시 시도해주세요.") String idToken, String name) {

  public AppleLoginCommand toServiceDto() {
    return new AppleLoginCommand(idToken, name);
  }
}
