package org.nexters.jaknaesoserver.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.nexters.jaknaesoserver.domain.auth.service.JwtService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final JwtService jwtService;

  @PostMapping("/reissue")
  public ApiResponse<TokenResponse> refreshToken(
      @RequestHeader(value = "Refresh-Token") String bearerRefreshToken) {
    TokenResponse response = jwtService.reissueToken(bearerRefreshToken);
    return ApiResponse.success(response);
  }
}
