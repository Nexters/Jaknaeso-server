package org.nexters.jaknaesocore.common.support.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  public static final CustomException INTERNAL_SERVER_ERROR =
      new CustomException(ErrorType.DEFAULT_ERROR);
  public static final CustomException TOKEN_EXPIRED = new CustomException(ErrorType.EXPIRED_TOKEN);
  public static final CustomException INVALID_TOKEN = new CustomException(ErrorType.INVALID_TOKEN);
  public static final CustomException UNSUPPORTED_TOKEN =
      new CustomException(ErrorType.UNSUPPORTED_TOKEN);
  public static final CustomException INCORRECT_TOKEN_FORMAT =
      new CustomException(ErrorType.INCORRECT_TOKEN_FORMAT);
  public static final CustomException INVALID_APPLE_ID_TOKEN =
      new CustomException(ErrorType.INVALID_APPLE_ID_TOKEN);
  public static final CustomException MEMBER_NOT_FOUND =
      new CustomException(ErrorType.MEMBER_NOT_FOUND);
  public static final CustomException SURVEY_OPTION_NOT_FOUND =
      new CustomException(ErrorType.SURVEY_OPTION_NOT_FOUND);
  public static final CustomException SURVEY_NOT_FOUND =
      new CustomException(ErrorType.SURVEY_NOT_FOUND);
  public static final CustomException NOT_READY_FOR_NEXT_BUNDLE =
      new CustomException(ErrorType.NOT_READY_FOR_NEXT_BUNDLE);
  public static final CustomException ALREADY_COMPLETED_SURVEY_BUNDLE =
      new CustomException(ErrorType.ALREADY_COMPLETED_SURVEY_BUNDLE);
  public static final CustomException FORBIDDEN_ACCESS = new CustomException(ErrorType.FORBIDDEN);

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
