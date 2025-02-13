package org.nexters.jaknaesoserver.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesoserver.domain.auth.model.JwtProperties;

class JwtParserTest {
  private static final String SECRET = "thisIsTestSecretKeyShouldBeLongEnoughForHS512";
  private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofDays(1);
  private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(30);

  private JwtParser jwtParser;
  private JwtProperties jwtProperties;

  @BeforeEach
  void setUp() {
    jwtProperties = new JwtProperties(SECRET, ACCESS_TOKEN_EXPIRATION, REFRESH_TOKEN_EXPIRATION);
    jwtParser = new JwtParser(jwtProperties);
  }

  @DisplayName("토큰에서 Subject를 추출한다")
  @Test
  void extractIdFromToken_Success() {
    // given
    Long userId = 1L;
    String token = createToken(userId);

    // when
    Long id = jwtParser.extractIdFromToken(token);

    // then
    assertThat(id).isEqualTo(1L);
  }

  @DisplayName("만료된 토큰이면 TOKEN_EXPIRED 예외가 발생한다")
  @Test
  void extractIdFromToken_ExpiredToken() {
    // given
    Long userId = 1L;
    String token = createExpiredToken(userId);

    // when & then
    assertThatThrownBy(() -> jwtParser.extractIdFromToken(token))
        .isInstanceOf(CustomException.class)
        .isEqualTo(CustomException.TOKEN_EXPIRED);
  }

  @DisplayName("유효하지 않은 토큰이면 INVALID_TOKEN 예외가 발생한다")
  @Test
  void extractIdFromToken_InvalidToken() {
    // given
    String invalidToken = "invalid.jwt.token";

    // when & then
    assertThatThrownBy(() -> jwtParser.extractIdFromToken(invalidToken))
        .isInstanceOf(CustomException.class)
        .isEqualTo(CustomException.INVALID_TOKEN);
  }

  private String createToken(Long userId) {
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .signWith(jwtProperties.getSecretKey())
        .compact();
  }

  private String createExpiredToken(Long userId) {
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .expiration(new Date(System.currentTimeMillis() - 1000))
        .signWith(jwtProperties.getSecretKey())
        .compact();
  }
}
