package org.nexters.jaknaesocore.common.support.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
  DEFAULT_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR,
      ErrorCode.E500,
      "An unexpected error has occurred.",
      LogLevel.ERROR),
  METHOD_ARGUMENT_NOT_VALID(
      HttpStatus.BAD_REQUEST, ErrorCode.E400, "Method argument validation failed.", LogLevel.DEBUG),
  BINDING_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E400, "Data binding failed.", LogLevel.WARN),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCode.E4011, "Token expired.", LogLevel.WARN),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "Invalid token.", LogLevel.WARN),
  UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "Unsupported token.", LogLevel.WARN),
  INCORRECT_TOKEN_FORMAT(
      HttpStatus.UNAUTHORIZED, ErrorCode.E401, "Request token format is incorrect.", LogLevel.WARN),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "Unauthorized.", LogLevel.WARN),
  FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCode.E403, "Forbidden.", LogLevel.WARN),
  SAMPLE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "Sample not found.", LogLevel.WARN),
  ;

  private final HttpStatus status;

  private final ErrorCode code;

  private final String message;

  private final LogLevel logLevel;

  ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {
    this.status = status;
    this.code = code;
    this.message = message;
    this.logLevel = logLevel;
  }
}
