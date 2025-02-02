package org.nexters.jaknaesocore.domain.auth.restclient;

import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "https://kauth.kakao.com")
public interface KakaoAuthClient {

  @PostExchange(
      url = "/oauth/token",
      contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8")
  KakaoTokenResponse requestToken(
      @RequestParam("grant_type") String grantType,
      @RequestParam("client_id") String clientId,
      @RequestParam("client_secret") String clientSecret,
      @RequestParam("code") String code,
      @RequestParam("redirect_uri") String redirectUri);
}
