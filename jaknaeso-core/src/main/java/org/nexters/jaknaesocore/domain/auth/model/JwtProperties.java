package org.nexters.jaknaesocore.domain.auth.model;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  private final String secret;
  private final Long accessTokenExpiration;
  private final Long refreshTokenExpiration;
  private final SecretKey secretKey;

  public JwtProperties(String secret, Long accessTokenExpiration, Long refreshTokenExpiration) {
    validateTokenExpirations(accessTokenExpiration, refreshTokenExpiration);
    this.secret = secret;
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
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
