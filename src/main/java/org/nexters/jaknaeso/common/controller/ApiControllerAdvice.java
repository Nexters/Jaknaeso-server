package org.nexters.jaknaeso.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.nexters.jaknaeso.common.support.error.CustomException;
import org.nexters.jaknaeso.common.support.error.ErrorType;
import org.nexters.jaknaeso.common.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error("CustomException : {}", e.getMessage(), e);
            case WARN -> log.warn("CustomException : {}", e.getMessage(), e);
            default -> log.info("CustomException : {}", e.getMessage(), e);
        }
        return new ResponseEntity<>(ApiResponse.error(e.getErrorType(), e.getData()), e.getErrorType().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.debug("Validation error occurred: {}", e.getMessage(), e);

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<Map<String, String>> validationErrors = fieldErrors.stream()
            .map(error -> Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage()
            ))
            .toList();

        return new ResponseEntity<>(ApiResponse.error(ErrorType.METHOD_ARGUMENT_NOT_VALID, validationErrors), ErrorType.METHOD_ARGUMENT_NOT_VALID.getStatus());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<?>> handleBindException(BindException e) {
        log.warn("Binding error occurred: {}", e.getMessage(), e);

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<Map<String, String>> validationErrors = fieldErrors.stream()
            .map(error -> Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage()
            ))
            .toList();

        return new ResponseEntity<>(ApiResponse.error(ErrorType.BINDING_ERROR, validationErrors), ErrorType.BINDING_ERROR.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResponse.error(ErrorType.DEFAULT_ERROR), ErrorType.DEFAULT_ERROR.getStatus());
    }

}
