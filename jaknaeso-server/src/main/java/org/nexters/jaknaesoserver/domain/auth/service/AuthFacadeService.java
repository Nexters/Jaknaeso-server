package org.nexters.jaknaesoserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.auth.service.OauthService;
import org.nexters.jaknaesocore.domain.auth.service.dto.AppleLoginCommand;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginWithTokenCommand;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesoserver.domain.auth.service.dto.LoginResponse;
import org.nexters.jaknaesoserver.domain.auth.service.dto.TokenResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthFacadeService {

  private final OauthService oauthService;
  private final JwtService jwtService;

  public LoginResponse appleLogin(final AppleLoginCommand command) {
    Member member = oauthService.appleLogin(command);
    TokenResponse tokenResponse = jwtService.issueToken(member.getId());
    return new LoginResponse(member.getId(), member.isCompletedOnboarding(), tokenResponse);
  }

  public LoginResponse kakaoLogin(final KakaoLoginCommand command) {
    Member member = oauthService.kakaoLogin(command);

    TokenResponse tokenResponse = jwtService.issueToken(member.getId());
    return new LoginResponse(member.getId(), member.isCompletedOnboarding(), tokenResponse);
  }

  public LoginResponse kakaoLoginWithToken(final KakaoLoginWithTokenCommand command) {
    Member member = oauthService.kakaoLoginWithToken(command);
    TokenResponse tokenResponse = jwtService.issueToken(member.getId());
    return new LoginResponse(member.getId(), member.isCompletedOnboarding(), tokenResponse);
  }

  public TokenResponse reissueToken(final String bearerRefreshToken) {
    return jwtService.reissueToken(bearerRefreshToken);
  }
}
