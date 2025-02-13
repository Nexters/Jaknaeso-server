package org.nexters.jaknaesoserver.domain.auth.service;

import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesoserver.domain.auth.model.JwtProperties;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

  private final JwtProperties jwtProperties;

  public String generateAccessToken(final Long userId) {
    return generateToken(Map.of(), userId.toString(), jwtProperties.getAccessTokenExpiration());
  }

  public String generateRefreshToken(final Long userId) {
    return generateToken(Map.of(), userId.toString(), jwtProperties.getRefreshTokenExpiration());
  }

  private String generateToken(
      final Map<String, Object> claims, final String subject, final Duration expirationDuration) {
    Date now = new Date();

    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expirationDuration.toMillis()))
        .signWith(jwtProperties.getSecretKey())
        .compact();
  }
}
