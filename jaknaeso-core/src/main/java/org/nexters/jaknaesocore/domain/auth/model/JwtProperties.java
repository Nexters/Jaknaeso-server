package org.nexters.jaknaesocore.domain.auth.model;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  private final String secret;
  private final Long accessTokenExpirationHours;
  private final Long refreshTokenExpirationHours;
  private final SecretKey secretKey;

  public JwtProperties(
      String secret, Long accessTokenExpirationHours, Long refreshTokenExpirationHours) {
    validateTokenExpirations(accessTokenExpirationHours, refreshTokenExpirationHours);
    this.secret = secret;
    this.accessTokenExpirationHours = accessTokenExpirationHours;
    this.refreshTokenExpirationHours = refreshTokenExpirationHours;
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
  }

  private void validateTokenExpirations(Long accessTokenExpiration, Long refreshTokenExpiration) {
    if (accessTokenExpiration <= 0 || refreshTokenExpiration <= 0) {
      throw new IllegalArgumentException("토큰의 만료시간은 0보다 커야 합니다.");
    }
    if (accessTokenExpiration >= refreshTokenExpiration) {
      throw new IllegalArgumentException("액세스 토큰의 만료시간은 리프레시 토큰의 만료시간보다 짧아야 합니다.");
    }
  }
}
