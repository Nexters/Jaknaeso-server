package org.nexters.jaknaesoserver.jwt;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtGenerator {

  private final JwtProperties jwtProperties;

  public String generateAccessToken(final Long id) {
    return generateToken(Map.of(), id.toString(), jwtProperties.getAccessTokenExpiration());
  }

  public String reissueAccessToken(final Long id) {
    return generateAccessToken(id);
  }

  public String generateRefreshToken(final Long id) {
    return generateToken(Map.of(), id.toString(), jwtProperties.getRefreshTokenExpiration());
  }

  private String generateToken(
      final Map<String, Object> claims, final String subject, final Long expiration) {
    Date now = new Date();

    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expiration))
        .signWith(jwtProperties.getSecretKey())
        .compact();
  }
}
