package org.nexters.jaknaesocore.domain.auth.restclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfoResponse(
    @JsonProperty("id") Long id, @JsonProperty("kakao_account") KakaoAccount kakaoAccount) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record KakaoAccount(
      @JsonProperty("profile") Profile profile, @JsonProperty("email") String email) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(@JsonProperty("nickname") String nickname) {}
  }
}
