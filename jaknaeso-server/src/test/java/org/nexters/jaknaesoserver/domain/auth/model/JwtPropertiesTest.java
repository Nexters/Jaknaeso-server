package org.nexters.jaknaesoserver.domain.auth.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtPropertiesTest {

  @DisplayName("액세스 토큰 만료시간은 0보다 커야 한다.")
  @Test
  void 액세스_토큰_만료시간은_0보다_커야_한다() {
    // given
    String secret = "thisIsTestSecretKeyShouldBeLongEnoughForHS512";
    Long accessTokenExpiration = 0L;
    Long refreshTokenExpiration = 36000000000L;
    // when & then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(() -> new JwtProperties(secret, accessTokenExpiration, refreshTokenExpiration))
        .withMessage("토큰의 만료시간은 0보다 커야 합니다.");
  }

  @DisplayName("리프레시 토큰 만료시간은 0보다 커야한다.")
  @Test
  void 리프레시_토큰_만료시간은_0보다_커야_한다() {
    // given
    String secret = "thisIsTestSecretKeyShouldBeLongEnoughForHS512";
    Long accessTokenExpiration = 0L;
    Long refreshTokenExpiration = 0L;
    // when & then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(() -> new JwtProperties(secret, accessTokenExpiration, refreshTokenExpiration))
        .withMessage("토큰의 만료시간은 0보다 커야 합니다.");
  }

  @DisplayName("액세스 토큰 만료시간은 리프레시 토큰 만료시간보다 짧아야 한다.")
  @Test
  void 액세스_토큰_만료시간은_리프레시_토큰_만료시간보다_짧아야_한다() {
    // given
    String secret = "thisIsTestSecretKeyShouldBeLongEnoughForHS512";
    Long accessTokenExpiration = 1000L;
    Long refreshTokenExpiration = 999L;
    // when & then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(() -> new JwtProperties(secret, accessTokenExpiration, refreshTokenExpiration))
        .withMessage("액세스 토큰의 만료시간은 리프레시 토큰의 만료시간보다 짧아야 합니다.");
  }
}
