package org.nexters.jaknaesoserver.common.httpinterface;

import java.util.Map;
import org.nexters.jaknaesoserver.common.httpinterface.errorhandler.KakaoClientErrorHandler;
import org.nexters.jaknaesoserver.common.httpinterface.errorhandler.RestClientErrorHandler;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class HttpInterfaceFactory {

  Map<String, RestClientErrorHandler> errorHandlers = Map.of(
      "https://kapi.kakao.com", new KakaoClientErrorHandler()
  );

  public <S> S create(final Class<S> httpClient) {
    HttpExchange httpExchange = AnnotationUtils.getAnnotation(httpClient, HttpExchange.class);

    validateHttpExchange(httpExchange);

    String url = httpExchange.url();
    RestClientErrorHandler errorHandler = getErrorHandler(url);
    RestClient restClient = RestClient.builder()
        .baseUrl(url)
        .defaultStatusHandler(HttpStatusCode::isError,
            (req, res) -> errorHandler.handleError(res.getStatusCode(), req, res))
        .build();

    return HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(restClient))
        .build()
        .createClient(httpClient);
  }

  private void validateHttpExchange(final HttpExchange httpExchange) {
    if (httpExchange == null) {
      throw new IllegalStateException("HttpExchange 애노테이션을 찾을 수 없습니다.");
    }
    if (!StringUtils.hasText(httpExchange.url())) {
      throw new IllegalArgumentException("HttpExchange 애노테이션의 url 값이 설정되지 않았습니다.");
    }
  }

  private RestClientErrorHandler getErrorHandler(final String url) {
    return errorHandlers.getOrDefault(url,
        (statusCode, req, res) -> new RestClientException(
            String.format("알 수 없는 오류가 발생했습니다. [%s] [%d]", url, statusCode.value())
        )
    );
  }
}
