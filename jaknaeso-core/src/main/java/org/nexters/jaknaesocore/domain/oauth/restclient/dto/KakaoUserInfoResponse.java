package org.nexters.jaknaesocore.domain.oauth.restclient.dto;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
    Long id,
    KakaoAccount kakaoAccount
) {

  public record KakaoAccount(
      String name
  ) {

  }
}
