package org.nexters.jaknaesocore.domain.auth.restclient;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoUserInfoResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "https://kapi.kakao.com")
public interface KakaoClient {

  @GetExchange("/v2/user/me")
  KakaoUserInfoResponse requestUserInfo(
      @RequestHeader(AUTHORIZATION) String authorization,
      @RequestHeader(CONTENT_TYPE) String contentType
  );
}
