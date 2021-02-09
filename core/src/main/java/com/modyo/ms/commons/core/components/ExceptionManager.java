package com.modyo.ms.commons.core.components;

import com.modyo.ms.commons.core.constants.ErrorCodes;
import com.modyo.ms.commons.core.dtos.ErrorDto;
import com.modyo.ms.commons.core.dtos.ErrorsResponseDto;
import com.modyo.ms.commons.core.dtos.RejectionDto;
import com.modyo.ms.commons.core.exceptions.BusinessErrorException;
import com.modyo.ms.commons.core.exceptions.CriticalBusinessErrorException;
import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import com.modyo.ms.commons.core.exceptions.ForbiddenException;
import com.modyo.ms.commons.core.exceptions.NotFoundException;
import com.modyo.ms.commons.core.exceptions.TechnicalErrorException;
import com.modyo.ms.commons.core.loggers.ErrorLogger;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Used for intercept thrown exceptions and return standard error responses
 */
@ControllerAdvice
public class ExceptionManager {

  /**
   * Unhandled errors
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorsResponseDto> handleException(Exception e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL, e);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorsResponseDto> handleException(NotFoundException e) {
    return logAndGetResponseEntity(HttpStatus.NOT_FOUND, ErrorCodes.RESOURCE_NOT_FOUND, e);
  }

  /**
   * When the business logic throws a business exception
   */
  @ExceptionHandler(BusinessErrorException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(BusinessErrorException e) {
    return logAndGetResponseEntity(HttpStatus.OK, ErrorCodes.BUSINESS_ERROR, e, e.getMessageCode());
  }

  /**
   * When the business logic throws a critiacl business exception
   */
  @ExceptionHandler(CriticalBusinessErrorException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(CriticalBusinessErrorException e) {
    return logAndGetResponseEntity(HttpStatus.OK, ErrorCodes.CRITICAL_BUSINESS_ERROR, e, e.getMessageCode());
  }

  /**
   * When request not includes required query params
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(
      MissingServletRequestParameterException e) {
    return logAndGetResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCodes.MISSING_PARAM, e);
  }

  /**
   * When bean validation is thrown due to invalid path or query params
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(ConstraintViolationException e) {
    List<RejectionDto> rejections = e.getConstraintViolations().stream()
        .map(violation -> new RejectionDto(violation.getPropertyPath().toString(), violation.getMessage()))
        .collect(Collectors.toList());
    return logAndGetResponseEntity(rejections, e);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorsResponseDto> handleException(MethodArgumentTypeMismatchException e) {
    List<RejectionDto> rejections = List.of(new RejectionDto(e.getName(), e.getMessage()));
    return logAndGetResponseEntity(rejections, e);
  }

  /**
   * When bean validation is thrown due to invalid body params
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(MethodArgumentNotValidException e) {
    List<RejectionDto> rejections = e.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> new RejectionDto(fieldError.getField(), fieldError.getDefaultMessage()))
        .collect(Collectors.toList());
    return logAndGetResponseEntity(rejections, e);
  }

  /**
   * When a custom validation is thrown due to invalid params
   */
  @ExceptionHandler(CustomValidationException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(CustomValidationException e) {
    return logAndGetResponseEntity(e.getRejections(), e);
  }

  /**
   * When request not includes required header
   */
  @ExceptionHandler(MissingRequestHeaderException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(MissingRequestHeaderException e) {
    return logAndGetResponseEntity(HttpStatus.BAD_REQUEST, ErrorCodes.MISSING_REQUEST_HEADER, e);
  }

  /**
   * When request method is not supported
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(
      HttpRequestMethodNotSupportedException e) {
    return logAndGetResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, ErrorCodes.METHOD_NOT_ALLOWED, e);
  }

  /**
   * When request content type is not supported
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpMediaTypeNotSupportedException e) {
    return logAndGetResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorCodes.UNSUPPORTED_MEDIA_TYPE, e);
  }

  /**
   * When request content type is not acceptable
   */
  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpMediaTypeNotAcceptableException e) {
    return logAndGetResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorCodes.UNSUPPORTED_MEDIA_TYPE, e);
  }

  /**
   * When request content is malformed
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpMessageNotReadableException e) {
    return logAndGetResponseEntity(HttpStatus.BAD_REQUEST, ErrorCodes.MALFORMED_REQUEST, e);
  }

  /**
   * When an external service response http status is a 5xx
   */
  @ExceptionHandler(HttpServerErrorException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpServerErrorException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.EXTERNAL_SERVICE, e);
  }

  /**
   * When an external service response http status is a 4xx
   */
  @ExceptionHandler(HttpClientErrorException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpClientErrorException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorCodes.MALFORMED_EXTERNAL_SERVICE_REQUEST, e);
  }

  /**
   * When an external service response http status is unknown
   */
  @ExceptionHandler(UnknownHttpStatusCodeException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(UnknownHttpStatusCodeException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorCodes.UNKNOWN_EXTERNAL_SERVICE_ERROR, e);
  }

  /**
   * When request to an external service fail
   */
  @ExceptionHandler(ResourceAccessException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(ResourceAccessException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorCodes.EXTERNAL_SERVICE_REQUEST_ERROR, e);
  }

  /**
   * When request is rejected due to missing or invalid permissions
   */
  @ExceptionHandler(ForbiddenException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(ForbiddenException e) {
    return logAndGetResponseEntity(HttpStatus.FORBIDDEN, ErrorCodes.ACCESS_FORBIDDEN, e);
  }

  /**
   * When a handled technical error exception is thrown
   */
  @ExceptionHandler(TechnicalErrorException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(TechnicalErrorException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.TECHNICAL_ERROR, e, e.getMessageCode());
  }

  private ResponseEntity<ErrorsResponseDto> logAndGetResponseEntity(
      HttpStatus httpStatus,
      ErrorCodes errorCode,
      Exception e) {
    return logAndGetResponseEntity(httpStatus, errorCode, e, null);
  }

  private ResponseEntity<ErrorsResponseDto> logAndGetResponseEntity(
      HttpStatus httpStatus,
      ErrorCodes errorCode,
      Exception e,
      String messageCode) {
    ErrorsResponseDto response = ErrorsResponseDto.builder()
        .error(ErrorDto.builder()
            .status(Integer.toString(httpStatus.value()))
            .code(errorCode.getCode())
            .messageCode(messageCode)
            .title(errorCode.getMessage())
            .detail(e.getMessage())
            .build())
        .build();
    log(response, e, httpStatus);
    return new ResponseEntity<>(response, httpStatus);
  }

  private ResponseEntity<ErrorsResponseDto> logAndGetResponseEntity(
      List<RejectionDto> rejections,
      Exception e) {
    ErrorsResponseDto response = ErrorsResponseDto.builder()
        .errors(rejections.stream().map(rejection ->
            ErrorDto.builder()
                .status(Integer.toString(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .code(ErrorCodes.INVALID_PARAM.getCode())
                .title(ErrorCodes.INVALID_PARAM.getMessage())
                .source(rejection.getSource())
                .detail(rejection.getDetail())
                .build())
            .collect(Collectors.toList()))
        .build();
    log(response, e, HttpStatus.UNPROCESSABLE_ENTITY);
    return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  private void log(ErrorsResponseDto response, Exception e, HttpStatus httpStatus) {
    ErrorLogger errorLog = ErrorLogger.builder()
        .className(e.getClass().getName())
        .stackTrace(ExceptionUtils.getStackTrace(e))
        .responseBody(response)
        .build();
    if (httpStatus.is5xxServerError()) {
      errorLog.logError();
    } else {
      errorLog.logInfo();
    }
  }
}
