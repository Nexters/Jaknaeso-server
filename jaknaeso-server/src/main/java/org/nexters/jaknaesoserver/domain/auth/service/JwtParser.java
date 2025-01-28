package org.nexters.jaknaesoserver.domain.auth.service;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesoserver.domain.auth.model.JwtProperties;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtParser {

  private final JwtProperties jwtProperties;

  public Long extractIdFromToken(String token) {

    Jws<Claims> claimsJws = parseClaims(token);

    String subject = claimsJws.getPayload().getSubject();
    return Long.parseLong(subject);
  }

  private Jws<Claims> parseClaims(String token) {
    try {
      return Jwts.parser()
          .verifyWith(jwtProperties.getSecretKey())
          .build()
          .parseSignedClaims(token);
    } catch (UnsupportedJwtException e) {
      throw CustomException.UNSUPPORTED_TOKEN;
    } catch (MalformedJwtException e) {
      throw CustomException.INVALID_TOKEN;
    } catch (ExpiredJwtException e) {
      throw CustomException.TOKEN_EXPIRED;
    } catch (Exception e) {
      throw CustomException.INTERNAL_SERVER_ERROR;
    }
  }
}
