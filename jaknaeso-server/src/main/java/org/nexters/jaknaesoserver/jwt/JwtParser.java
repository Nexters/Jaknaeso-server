package org.nexters.jaknaesoserver.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
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
    } catch (ExpiredJwtException e) {
      throw CustomException.TOKEN_EXPIRED;
    } catch (MalformedJwtException e) {
      throw CustomException.INVALID_TOKEN;
    } catch (UnsupportedJwtException e) {
      throw CustomException.UNSUPPORTED_TOKEN;
    } catch (IllegalArgumentException e) {
      throw CustomException.EMPTY_TOKEN;
    } catch (Exception e) {
      throw new CustomException(ErrorType.DEFAULT_ERROR);
    }
  }
}
