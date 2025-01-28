package org.nexters.jaknaesoserver.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.auth.dto.KakaoLoginRequest;
import org.nexters.jaknaesocore.domain.auth.dto.KakaoLoginResponse;
import org.nexters.jaknaesocore.domain.auth.service.AuthService;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.nexters.jaknaesoserver.domain.auth.service.JwtService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;

  @ResponseStatus(code = HttpStatus.OK)
  @PostMapping("kakao-login")
  public KakaoLoginResponse kakaoLogin(@RequestBody final KakaoLoginRequest request) {
    return authService.kakaoLogin(request.toServiceDto());
  }

  @PostMapping("/reissue")
  public ApiResponse<TokenResponse> refreshToken(
      @RequestHeader(value = "Refresh-Token") String bearerRefreshToken) {
    TokenResponse response = jwtService.reissueToken(bearerRefreshToken);
    return ApiResponse.success(response);
  }
}
