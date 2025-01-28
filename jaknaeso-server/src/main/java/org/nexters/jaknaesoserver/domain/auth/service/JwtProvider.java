package org.nexters.jaknaesoserver.domain.auth.service;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesoserver.domain.auth.model.JwtProperties;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

  private final JwtProperties jwtProperties;

  public String generateAccessToken(final Long userId) {
    return generateToken(
        Map.of(), userId.toString(), jwtProperties.getAccessTokenExpirationHours());
  }

  public String generateRefreshToken(final Long userId) {
    return generateToken(
        Map.of(), userId.toString(), jwtProperties.getRefreshTokenExpirationHours());
  }

  private String generateToken(
      final Map<String, Object> claims, final String subject, final Long expirationHours) {
    Date now = new Date();
    long expirationMillis = TimeUnit.HOURS.toMillis(expirationHours);

    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expirationMillis))
        .signWith(jwtProperties.getSecretKey())
        .compact();
  }
}
