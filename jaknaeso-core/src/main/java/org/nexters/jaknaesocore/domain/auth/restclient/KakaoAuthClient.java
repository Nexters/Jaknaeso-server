package org.nexters.jaknaesocore.domain.auth.restclient;

import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "https://kauth.kakao.com")
public interface KakaoAuthClient {

  @PostExchange(
      url = "/oauth/token",
      contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8")
  KakaoTokenResponse requestToken(@RequestBody MultiValueMap<String, String> params);
}
