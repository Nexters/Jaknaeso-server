package org.nexters.jaknaesocore.common.support.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
    DEFAULT_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    METHOD_ARGUMENT_NOT_VALID(
            HttpStatus.BAD_REQUEST, ErrorCode.E400, "Method argument validation failed.", LogLevel.DEBUG),
    BINDING_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E400, "Data binding failed.", LogLevel.WARN),
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
