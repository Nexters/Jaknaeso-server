package org.nexters.jaknaesocore.domain.auth.restclient;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoTokenCommand;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoTokenResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "https://kauth.kakao.com")
public interface KakaoAuthClient {

  @PostExchange("/oauth/token")
  KakaoTokenResponse requestToken(
      @RequestHeader(CONTENT_TYPE) String contentType, @RequestBody KakaoTokenCommand command);
}
