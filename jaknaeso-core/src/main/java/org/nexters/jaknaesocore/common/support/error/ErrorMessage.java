package org.nexters.jaknaesocore.common.support.error;

import lombok.Getter;

@Getter
public class ErrorMessage {

  private final String code;

  private final String message;

  private final Object data;

  private final String traceId;

  public ErrorMessage(ErrorType errorType) {
    this.code = errorType.getCode().name();
    this.message = errorType.getMessage();
    this.data = null;
    this.traceId = null;
  }

  public ErrorMessage(ErrorType errorType, Object data) {
    this.code = errorType.getCode().name();
    this.message = errorType.getMessage();
    this.data = data;
    this.traceId = null;
  }

  public ErrorMessage(ErrorType errorType, Object data, String traceId) {
    this.code = errorType.getCode().name();
    this.message = errorType.getMessage();
    this.data = data;
    this.traceId = traceId;
  }
}
