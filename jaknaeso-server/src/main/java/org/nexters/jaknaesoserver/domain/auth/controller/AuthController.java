package org.nexters.jaknaesoserver.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesoserver.domain.auth.controller.dto.AppleLoginRequest;
import org.nexters.jaknaesoserver.domain.auth.controller.dto.KakaoLoginRequest;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.nexters.jaknaesoserver.domain.auth.service.AuthFacadeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

  private final AuthFacadeService authFacadeService;

  @PostMapping("/apple-login")
  public ApiResponse<TokenResponse> appleLogin(
      @RequestBody @Valid final AppleLoginRequest request) {
    final TokenResponse response = authFacadeService.appleLogin(request.toServiceDto());
    return ApiResponse.success(response);
  }

  @PostMapping("/kakao-login")
  public ApiResponse<TokenResponse> kakaoLogin(@RequestBody @Valid KakaoLoginRequest request) {
    TokenResponse response = authFacadeService.kakaoLogin(request.toServiceDto());
    return ApiResponse.success(response);
  }

  @PostMapping("/reissue")
  public ApiResponse<TokenResponse> refreshToken(
      @RequestHeader(value = "Refresh-Token") final String bearerRefreshToken) {
    TokenResponse response = authFacadeService.reissueToken(bearerRefreshToken);
    return ApiResponse.success(response);
  }
}
