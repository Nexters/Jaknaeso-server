package org.nexters.jaknaesocore.common.support.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
  // default
  DEFAULT_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "예기치 않은 오류가 발생했습니다.", LogLevel.ERROR),
  METHOD_ARGUMENT_NOT_VALID(
      HttpStatus.BAD_REQUEST, ErrorCode.E400, "입력하신 데이터가 올바르지 않습니다.", LogLevel.INFO),
  BINDING_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E400, "입력 형식이 올바르지 않습니다.", LogLevel.WARN),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "요청하신 리소스를 찾을 수 없습니다.", LogLevel.WARN),

  // token
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCode.E4011, "토큰이 만료되었습니다.", LogLevel.INFO),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "유효하지 않은 토큰입니다.", LogLevel.WARN),
  UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "지원하지 않는 형식의 토큰입니다.", LogLevel.WARN),
  INCORRECT_TOKEN_FORMAT(
      HttpStatus.UNAUTHORIZED, ErrorCode.E401, "토큰이 비어있거나 형식이 잘못되었습니다.", LogLevel.WARN),

  // auth
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "인증되지 않은 접근입니다.", LogLevel.WARN),
  FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCode.E403, "접근 권한이 없습니다.", LogLevel.WARN),

  // member
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "존재하지 않는 멤버입니다.", LogLevel.WARN),

  // social account
  SOCIAL_ACCOUNT_NOT_FOUND(
      HttpStatus.NOT_FOUND, ErrorCode.E404, "존재하지 않는 소셜 계정입니다.", LogLevel.WARN),

  // business
  INVALID_APPLE_ID_TOKEN(
      HttpStatus.BAD_REQUEST, ErrorCode.E400, "유효하지 않은 애플로그인입니다.", LogLevel.WARN),

  SURVEY_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "존재하지 않는 설문 옵션입니다.", LogLevel.WARN),

  SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "존재하지 않는 설문입니다.", LogLevel.WARN),

  // survey
  NO_MORE_SURVEY_BUNDLES(
      HttpStatus.BAD_REQUEST,
      ErrorCode.E400,
      "다음 번들을 진행할 준비가 되지 않았습니다. 추가로 진행할 번들이 필요합니다.",
      LogLevel.ERROR),

  ALREADY_COMPLETED_SURVEY_BUNDLE(
      HttpStatus.BAD_REQUEST, ErrorCode.E400, "이미 완료된 설문 번들입니다.", LogLevel.WARN),
  BUNDLE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "해당 번들을 찾을 수 없습니다.", LogLevel.WARN),

  // character
  CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "존재하지 않는 캐릭터 기록입니다.", LogLevel.WARN),
  CHARACTER_VALUE_REPORT_NOT_FOUND(
      HttpStatus.NOT_FOUND, ErrorCode.E404, "존재하지 않는 캐릭터 가치관 분석 기록입니다.", LogLevel.WARN),

  NOT_PROCEED_ONBOARDING(
      HttpStatus.FORBIDDEN, ErrorCode.E403, "온보딩을 진행해야 접근 가능합니다.", LogLevel.ERROR),
  INVALID_KEYWORD_SCORE(HttpStatus.NOT_FOUND, ErrorCode.E404, "키워드 점수가 잘못되었습니다.", LogLevel.ERROR);

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
