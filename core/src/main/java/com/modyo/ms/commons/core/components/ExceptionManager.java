package com.modyo.ms.commons.core.components;

import com.modyo.ms.commons.core.constants.ErrorCodes;
import com.modyo.ms.commons.core.dtos.ErrorDto;
import com.modyo.ms.commons.core.dtos.ErrorsResponseDto;
import com.modyo.ms.commons.core.dtos.RejectionDto;
import com.modyo.ms.commons.core.exceptions.BusinessErrorException;
import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import com.modyo.ms.commons.core.exceptions.ForbiddenException;
import com.modyo.ms.commons.core.exceptions.TechnicalErrorException;
import com.modyo.ms.commons.core.loggers.ErrorLogger;
import java.util.Collections;
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
 * Clase que engloba la configuración de mapeo de Excepciones a respuestas HTTP
 */
@ControllerAdvice
public class ExceptionManager {

  /**
   * Excepciones   no manejadas
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorsResponseDto> handleException(Exception e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNO, e);
  }

  /**
   * Excepciones encontradas como parte de la lógica de negocio
   */
  @ExceptionHandler(BusinessErrorException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(BusinessErrorException e) {
    return logAndGetResponseEntity(HttpStatus.OK, ErrorCodes.NEGOCIO, e);
  }

  /**
   * Excepciones por parámetros requeridos faltantes de tipo query
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(
      MissingServletRequestParameterException e) {
    return logAndGetResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCodes.PARAMETRO_FALTANTE, e);
  }

  /**
   * Excepciones de Bean Validation sobre parámetros de tipo path y query
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(ConstraintViolationException e) {
    List<RejectionDto> rejections = e.getConstraintViolations().stream().map(violation ->
        RejectionDto.builder()
            .source(violation.getPropertyPath().toString())
            .detail(violation.getMessage()).build())
        .collect(Collectors.toList());
    return logAndGetResponseEntity(rejections, e);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorsResponseDto> handleException(MethodArgumentTypeMismatchException e) {
    List<RejectionDto> rejections = Collections.singletonList(RejectionDto.builder()
        .source(e.getName())
        .detail(e.getMessage())
        .build());
    return logAndGetResponseEntity(rejections, e);
  }

  /**
   * Excepciones de Bean Validation sobre parámetros de un DTO
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(MethodArgumentNotValidException e) {
    List<RejectionDto> rejections = e.getBindingResult().getFieldErrors().stream().map(fieldError ->
        RejectionDto.builder()
            .source(fieldError.getField())
            .detail(fieldError.getDefaultMessage())
            .build())
        .collect(Collectors.toList());
    return logAndGetResponseEntity(rejections, e);
  }

  /**
   * Excepciones de validación de parámetros que no usan Bean Validation
   */
  @ExceptionHandler(CustomValidationException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(CustomValidationException e) {
    return logAndGetResponseEntity(e.getRejections(), e);
  }

  /**
   * Excepciones de validacion de presencia de headers en una request
   */
  @ExceptionHandler(MissingRequestHeaderException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(MissingRequestHeaderException e) {
    return logAndGetResponseEntity(HttpStatus.BAD_REQUEST, ErrorCodes.HEADER_FALTANTE, e);
  }

  /**
   * Excepciones por llamadas a métodos no soportados por la API
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(
      HttpRequestMethodNotSupportedException e) {
    return logAndGetResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, ErrorCodes.METODO_NO_SOPORTADO, e);
  }

  /**
   * Excepciones por llamadas a métodos con content-type no soportado
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpMediaTypeNotSupportedException e) {
    return logAndGetResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorCodes.FORMATO_NO_SOPORTADO, e);
  }

  /**
   * Excepciones por llamadas a métodos con content-type no aceptado
   */
  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpMediaTypeNotAcceptableException e) {
    return logAndGetResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorCodes.FORMATO_NO_SOPORTADO, e);
  }

  /**
   * Excepciones por llamadas a métodos con un json mal formado
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpMessageNotReadableException e) {
    return logAndGetResponseEntity(HttpStatus.BAD_REQUEST, ErrorCodes.OBJETO_MAL_FORMADO, e);
  }

  /**
   * Excepciones por respuestas no exitosas de servicios externos a causa de un problemas propios de los servicios
   */
  @ExceptionHandler(HttpServerErrorException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpServerErrorException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.SERVICIO_EXTERNO, e);
  }

  /**
   * Excepciones por respuestas no exitosas de un servicio externo a causa de consultas mal formadas
   */
  @ExceptionHandler(HttpClientErrorException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(HttpClientErrorException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorCodes.CONSULTA_SERVICIO_MAL_FORMADA, e);
  }

  /**
   * Excepciones por respuestas no exitosas de servicios externos con código de error desconocido
   */
  @ExceptionHandler(UnknownHttpStatusCodeException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(UnknownHttpStatusCodeException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorCodes.SERVICIO_EXTERNO_DESCONOCIDO, e);
  }

  /**
   * Excepciones por consultas no exitosas a servicios externos
   */
  @ExceptionHandler(ResourceAccessException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(ResourceAccessException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorCodes.CONSULTA_SERVICIO_EXTERNO, e);
  }

  /**
   * Excepciones por consultas rechazadas
   */
  @ExceptionHandler(ForbiddenException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(ForbiddenException e) {
    return logAndGetResponseEntity(HttpStatus.FORBIDDEN, ErrorCodes.ACCESO_PROHIBIDO, e);
  }

  /**
   * Excepciones durante algún procesamiento de datos
   */
  @ExceptionHandler(TechnicalErrorException.class)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ResponseEntity<ErrorsResponseDto> handleException(TechnicalErrorException e) {
    return logAndGetResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.TECNICO, e);
  }

  private ResponseEntity<ErrorsResponseDto> logAndGetResponseEntity(
      HttpStatus httpStatus,
      ErrorCodes errorCode,
      Exception e) {
    ErrorsResponseDto response = ErrorsResponseDto.builder()
        .error(ErrorDto.builder()
            .status(Integer.toString(httpStatus.value()))
            .code(errorCode.getCode())
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
                .code(ErrorCodes.PARAMETRO_NO_VALIDO.getCode())
                .title(ErrorCodes.PARAMETRO_NO_VALIDO.getMessage())
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
