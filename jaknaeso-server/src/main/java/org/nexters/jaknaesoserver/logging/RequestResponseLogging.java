package org.nexters.jaknaesoserver.logging;

import java.util.Map;
import lombok.Getter;

@Getter
public class RequestResponseLogging {
  String traceId;
  String className;
  String methodName;
  String httpMethod;
  String uri;
  Map<String, String> params;
  Object requestBody;
  Object responseBody;

  public RequestResponseLogging(
      String traceId,
      String className,
      String methodName,
      String httpMethod,
      String uri,
      Map<String, String> params,
      Object requestBody,
      Object responseBody) {
    this.traceId = traceId;
    this.className = className;
    this.methodName = methodName;
    this.httpMethod = httpMethod;
    this.uri = uri;
    this.params = params;
    this.requestBody = requestBody;
    this.responseBody = responseBody;
  }

  public void updateResponseBody(Object responseBody) {
    this.responseBody = responseBody;
  }
}
