package org.nexters.jaknaesoserver.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.nexters.jaknaesoserver.domain.auth.service.AuthFacadeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

  private final AuthFacadeService authFacadeService;

  @GetMapping("/kakao-login")
  public ApiResponse<TokenResponse> kakaoLogin(
      @RequestParam(required = false) final String code,
      @RequestParam(required = false) final String error) {
    if (error != null) {
      ApiResponse.error(ErrorType.INVALID_KAKAO_CODE);
    }
    TokenResponse response = authFacadeService.kakaoLogin(new KakaoLoginCommand(code));
    return ApiResponse.success(response);
  }

  @PostMapping("/reissue")
  public ApiResponse<TokenResponse> refreshToken(
      @RequestHeader(value = "Refresh-Token") final String bearerRefreshToken) {
    TokenResponse response = authFacadeService.reissueToken(bearerRefreshToken);
    return ApiResponse.success(response);
  }
}
