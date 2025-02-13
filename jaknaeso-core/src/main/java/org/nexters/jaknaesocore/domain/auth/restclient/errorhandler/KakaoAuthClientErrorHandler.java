package org.nexters.jaknaesocore.domain.auth.restclient.errorhandler;

import lombok.extern.slf4j.Slf4j;
import org.nexters.jaknaesocore.common.http.errorhandler.RestClientErrorHandler;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;

@Slf4j
public class KakaoAuthClientErrorHandler implements RestClientErrorHandler {

  @Override
  public void handleError(
      HttpStatusCode statusCode, HttpRequest request, ClientHttpResponse response) {
    log.warn("Request URI: {}", request.getURI());
    if (statusCode.value() == 401) {
      throw new RestClientException("인증에 실패했습니다. 카카오 인가 코드를 확인해주세요.");
    }
    throw new RestClientException("카카오 API 요청 중 알 수 없는 오류가 발생했습니다.");
  }
}
