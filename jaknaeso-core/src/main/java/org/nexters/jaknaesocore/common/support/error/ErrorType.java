package org.nexters.jaknaesocore.common.support.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
  DEFAULT_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "예기치 않은 오류가 발생했습니다.", LogLevel.ERROR),
  METHOD_ARGUMENT_NOT_VALID(
      HttpStatus.BAD_REQUEST, ErrorCode.E400, "입력하신 데이터가 올바르지 않습니다.", LogLevel.DEBUG),
  BINDING_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E400, "입력 형식이 올바르지 않습니다.", LogLevel.WARN),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCode.E4011, "토큰이 만료되었습니다.", LogLevel.WARN),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "유효하지 않은 토큰입니다.", LogLevel.WARN),
  UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "지원하지 않는 형식의 토큰입니다.", LogLevel.WARN),
  INCORRECT_TOKEN_FORMAT(
      HttpStatus.UNAUTHORIZED, ErrorCode.E401, "토큰이 비어있거나 형식이 잘못되었습니다.", LogLevel.WARN),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "인증되지 않은 접근입니다.", LogLevel.WARN),
  FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCode.E403, "접근 권한이 없습니다.", LogLevel.WARN),
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
