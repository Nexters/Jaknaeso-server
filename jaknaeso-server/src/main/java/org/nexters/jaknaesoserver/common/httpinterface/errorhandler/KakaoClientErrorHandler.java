package org.nexters.jaknaesoserver.common.httpinterface.errorhandler;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientException;

public class KakaoClientErrorHandler implements RestClientErrorHandler {

  @Override
  public void handleError(HttpStatusCode statusCode, Object request, Object response) {
    if (statusCode.value() == 401) {
      throw new RestClientException("인증에 실패했습니다. 카카오 API 토큰을 확인해주세요.");
    }
    throw new RestClientException("카카오 API 요청 중 알 수 없는 오류가 발생했습니다.");
  }
}
