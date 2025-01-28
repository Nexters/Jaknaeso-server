package org.nexters.jaknaesocore.common.http.errorhandler;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

public interface RestClientErrorHandler {

  void handleError(HttpStatusCode statusCode, HttpRequest request, ClientHttpResponse response);
}
