package org.nexters.jaknaesoserver.common.httpinterface;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class HttpInterfaceFactory {

  public <S> S create(final Class<S> httpClient) {
    HttpExchange httpExchange = AnnotationUtils.getAnnotation(httpClient, HttpExchange.class);
    if (httpExchange == null) {
      throw new IllegalStateException("HttpExchange 애노테이션을 찾을 수 없습니다.");
    }

    String url = httpExchange.url();
    if (!StringUtils.hasText(url)) {
      throw new IllegalArgumentException("HttpExchange 애노테이션의 url 값이 설정되지 않았습니다.");
    }

    RestClient restClient = RestClient.builder()
        .baseUrl(url)
        .build();

    return HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(restClient))
        .build()
        .createClient(httpClient);
  }
}
