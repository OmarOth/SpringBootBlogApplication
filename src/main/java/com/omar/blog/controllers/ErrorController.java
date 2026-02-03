package com.omar.blog.controllers;

import com.omar.blog.domain.dto.ApiErrorResponse;
import com.omar.blog.domain.dto.ApiErrorResponse.FieldError;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    ApiErrorResponse apiErrorResponse =
        ApiErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();

    return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex) {
    ApiErrorResponse apiErrorResponse =
        ApiErrorResponse.builder()
            .status(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .build();

    return new ResponseEntity<>(apiErrorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(
      BadCredentialsException ex) {
    ApiErrorResponse apiErrorResponse =
        ApiErrorResponse.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .message("Incorrect username or password")
            .build();

    return new ResponseEntity<>(apiErrorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(
      EntityNotFoundException ex) {
    ApiErrorResponse apiErrorResponse =
        ApiErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .build();

    return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {

    List<FieldError> errors = new ArrayList<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            fieldError ->
                errors.add(
                    new ApiErrorResponse.FieldError(
                        fieldError.getField(), fieldError.getDefaultMessage())));

    ApiErrorResponse apiErrorResponse =
        ApiErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Validation Failed")
            .errors(errors)
            .build();

    return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
    log.error("Caught exception", ex);
    ApiErrorResponse apiErrorResponse =
        ApiErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("An unexpected error occurred")
            .build();

    return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
