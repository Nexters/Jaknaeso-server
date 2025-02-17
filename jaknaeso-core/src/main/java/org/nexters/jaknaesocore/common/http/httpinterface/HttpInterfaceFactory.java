package org.nexters.jaknaesocore.common.http.httpinterface;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Profile("!test")
@Component
public class HttpInterfaceFactory {

  public <S> S create(final Class<S> httpClient) {
    HttpExchange httpExchange = AnnotationUtils.getAnnotation(httpClient, HttpExchange.class);

    validateHttpExchange(httpClient, httpExchange);

    String url = httpExchange.url();
    RestClient restClient =
        RestClient.builder()
            .baseUrl(url)
            .messageConverters(
                converters -> {
                  converters.add(new ByteArrayHttpMessageConverter());
                  converters.add(new StringHttpMessageConverter());
                  converters.add(new FormHttpMessageConverter());
                  converters.add(new MappingJackson2HttpMessageConverter());
                })
            .build();

    return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
        .build()
        .createClient(httpClient);
  }

  private void validateHttpExchange(final Object httpClient, final HttpExchange httpExchange) {
    if (httpExchange == null) {
      throw new IllegalStateException("HttpExchange 애노테이션을 찾을 수 없습니다.");
    }
    if (!StringUtils.hasText(httpExchange.url())) {
      throw new IllegalArgumentException("HttpExchange 애노테이션의 url 값이 설정되지 않았습니다.");
    }
  }
}
