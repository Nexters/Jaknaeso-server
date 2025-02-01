package org.nexters.jaknaesoserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.auth.service.OauthService;
import org.nexters.jaknaesocore.domain.auth.service.dto.AppleLoginCommand;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthFacadeService {

  private final OauthService oauthService;
  private final JwtService jwtService;

  public TokenResponse appleLogin(final AppleLoginCommand command) {
    final Long memberId = oauthService.appleLogin(command);
    return jwtService.issueToken(memberId);
  }

  public TokenResponse kakaoLogin(final KakaoLoginCommand kakaoLoginCommand) {
    Long memberId = oauthService.kakaoLogin(kakaoLoginCommand);
    return jwtService.issueToken(memberId);
  }

  public TokenResponse reissueToken(final String bearerRefreshToken) {
    return jwtService.reissueToken(bearerRefreshToken);
  }
}
