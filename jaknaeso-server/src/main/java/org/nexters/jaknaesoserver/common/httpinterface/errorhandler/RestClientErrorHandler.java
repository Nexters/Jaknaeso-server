package org.nexters.jaknaesoserver.common.httpinterface.errorhandler;

import org.springframework.http.HttpStatusCode;

public interface RestClientErrorHandler {

  void handleError(HttpStatusCode statusCode, Object request, Object response);
}
