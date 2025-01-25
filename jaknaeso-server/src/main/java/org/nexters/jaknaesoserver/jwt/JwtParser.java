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
    try {
      io.jsonwebtoken.JwtParser jwtParser =
          Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build();

      String subject = jwtParser.parseSignedClaims(token).getPayload().getSubject();
      return Long.parseLong(subject);
    } catch (ExpiredJwtException e) {
      throw CustomException.TOKEN_EXPIRED;
    } catch (MalformedJwtException | NumberFormatException e) {
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
