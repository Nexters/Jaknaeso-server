package org.nexters.jaknaesoserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtService {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtParser jwtParser;
  private final JwtProvider jwtProvider;

  public TokenResponse issueToken(final Long userId) {
    String accessToken = jwtProvider.generateAccessToken(userId);
    String refreshToken = jwtProvider.generateRefreshToken(userId);
    return TokenResponse.of(userId, accessToken, refreshToken);
  }

  public TokenResponse reissueToken(final String bearerRefreshToken) {
    String refreshToken = bearerRefreshToken.replace(BEARER_PREFIX, "");

    Long userId = jwtParser.extractIdFromToken(refreshToken);
    String accessToken = jwtProvider.generateAccessToken(userId);
    String newRefreshToken = jwtProvider.generateRefreshToken(userId);
    return TokenResponse.of(userId, accessToken, newRefreshToken);
  }
}
