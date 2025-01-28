package org.nexters.jaknaesoserver.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.auth.dto.TokenResponse;
import org.nexters.jaknaesocore.domain.auth.service.JwtService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

  private final JwtService jwtService;

  @PostMapping("/reissue")
  public ApiResponse<TokenResponse> refreshToken(
      @RequestHeader(value = "Refresh-Token") String refreshToken) {
    TokenResponse response = jwtService.reissueToken(refreshToken);
    return ApiResponse.success(response);
  }
}
