package org.nexters.jaknaesocore.common.support.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  public static final CustomException SAMPLE_NOT_FOUND =
      new CustomException(ErrorType.SAMPLE_NOT_FOUND);
  public static final CustomException TOKEN_EXPIRED = new CustomException(ErrorType.EXPIRED_TOKEN);
  public static final CustomException INVALID_TOKEN = new CustomException(ErrorType.INVALID_TOKEN);
  public static final CustomException UNSUPPORTED_TOKEN =
      new CustomException(ErrorType.UNSUPPORTED_TOKEN);
  public static final CustomException EMPTY_TOKEN = new CustomException(ErrorType.EMPTY_TOKEN);

  private final ErrorType errorType;

  private final Object data;

  public CustomException(ErrorType errorType) {
    super(errorType.getMessage());
    this.errorType = errorType;
    this.data = null;
  }

  public CustomException(ErrorType errorType, Object data) {
    super(errorType.getMessage());
    this.errorType = errorType;
    this.data = data;
  }
}
