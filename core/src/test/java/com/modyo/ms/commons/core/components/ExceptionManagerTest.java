package com.modyo.ms.commons.core.components;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.modyo.ms.commons.core.InMemoryTestRequestAttributes;
import com.modyo.ms.commons.core.constants.ErrorCodes;
import com.modyo.ms.commons.core.dtos.ErrorsResponseDto;
import com.modyo.ms.commons.core.dtos.RejectionDto;
import com.modyo.ms.commons.core.exceptions.BusinessErrorException;
import com.modyo.ms.commons.core.exceptions.CriticalBusinessErrorException;
import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import com.modyo.ms.commons.core.exceptions.ForbiddenException;
import com.modyo.ms.commons.core.exceptions.NotFoundException;
import com.modyo.ms.commons.core.exceptions.TechnicalErrorException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collections;
import javax.validation.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public class ExceptionManagerTest {

  private ExceptionManager managerUnderTest = new ExceptionManager();

  @Before
  public void setUp() {
    RequestContextHolder.setRequestAttributes(new InMemoryTestRequestAttributes());
    RequestContextHolder.currentRequestAttributes().setAttribute("correlationId", "corrId", 0);
  }

  @Test
  public void givenExceptionNotDefined_ThenReturn500_1000() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new NullPointerException());

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.INTERNO.getCode()));
  }

  @Test
  public void givenBusinessErrorException_ThenReturn200_1010() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new BusinessErrorException("business error", "001"));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertNotNull(responseEntity.getBody().getErrors());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.BUSINESS_ERROR.getCode()));
    assertThat(responseEntity.getBody().getErrors().get(0).getMessageCode(), is("001"));
  }

  @Test
  public void givenCriticalBusinessErrorException_ThenReturn200_1010() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new CriticalBusinessErrorException("business error", "001"));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertNotNull(responseEntity.getBody().getErrors());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.CRITICAL_BUSINESS_ERROR.getCode()));
    assertThat(responseEntity.getBody().getErrors().get(0).getMessageCode(), is("001"));
  }

  @Test
  public void givenNotFoundException_ThenReturn404() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new NotFoundException());

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    assertNotNull(responseEntity.getBody().getErrors());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.RESOURCE_NOT_FOUND.getCode()));
  }

  @Test
  public void givenMissingServletRequestParameterException_ThenReturn422_1011() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new MissingServletRequestParameterException("name", "type"));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.PARAMETRO_FALTANTE.getCode()));
  }

  @Test
  public void givenConstraintViolationException_ThenReturn422() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new ConstraintViolationException("violated", Collections.emptySet()));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
  }

  @Test
  public void givenMethodArgumentTypeMismatchException_ThenReturn422_1001() {
    ExceptionManager exceptionManager = new ExceptionManager();

    ResponseEntity<ErrorsResponseDto> responseEntity = exceptionManager.handleException(
        new MethodArgumentTypeMismatchException(null, null, null, null, null));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.PARAMETRO_NO_VALIDO.getCode()));
  }

  @Test
  public void givenCustomValidationException_ThenReturn422_1001() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new CustomValidationException(Collections.singletonList(
            new RejectionDto("source", "detail")
        )));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.PARAMETRO_NO_VALIDO.getCode()));
  }

  @Test
  public void givenMissingRequestHeaderException_ThenReturn400_1009() throws NoSuchMethodException {
    Method method = String.class.getMethod("toString");
    HandlerMethod handlerMethod = new HandlerMethod("", method);
    MethodParameter methodReturnType = handlerMethod.getReturnType();

    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new MissingRequestHeaderException("headername", methodReturnType));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.HEADER_FALTANTE.getCode()));
  }

  @Test
  public void givenHttpRequestMethodNotSupportedException_ThenReturn405_1002() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new HttpRequestMethodNotSupportedException("not supported"));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.METHOD_NOT_ALLOWED));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.METODO_NO_SOPORTADO.getCode()));
  }

  @Test
  public void givenHttpMediaTypeNotSupportedException_ThenReturn415_1003() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new HttpMediaTypeNotSupportedException("not supported"));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.FORMATO_NO_SOPORTADO.getCode()));
  }

  @Test
  public void givenHttpMediaTypeNotAcceptableException_ThenReturn415_1003() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new HttpMediaTypeNotAcceptableException("not supported"));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.FORMATO_NO_SOPORTADO.getCode()));
  }

  @Test
  public void givenHttpMessageNotReadableException_ThenReturn400_1004() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new HttpMessageNotReadableException("message not readable"));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.OBJETO_MAL_FORMADO.getCode()));
  }

  @Test
  public void givenHttpServerErrorException_ThenReturn500_1005() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.SERVICIO_EXTERNO.getCode()));
  }

  @Test
  public void givenHttpClientErrorException_ThenReturn500_1006() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.CONSULTA_SERVICIO_MAL_FORMADA.getCode()));
  }

  @Test
  public void givenUnknownHttpStatusCodeException_ThenReturn500_1007() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new UnknownHttpStatusCodeException(499, "unknown", HttpHeaders.EMPTY, new byte[0], Charset.defaultCharset()));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.SERVICIO_EXTERNO_DESCONOCIDO.getCode()));
  }

  @Test
  public void givenResourceAccessException_ThenReturn500_1009() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new ResourceAccessException("no access"));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.CONSULTA_SERVICIO_EXTERNO.getCode()));
  }

  @Test
  public void givenForbiddenException_ThenReturn403_1012() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new ForbiddenException("forbidden"));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.ACCESO_PROHIBIDO.getCode()));
  }

  @Test
  public void givenTechnicalErrorException_ThenReturn403_1013() {
    ResponseEntity<ErrorsResponseDto> responseEntity = managerUnderTest.handleException(
        new TechnicalErrorException("technical", new NullPointerException()));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody().getErrors().size(), is(1));
    assertThat(responseEntity.getBody().getErrors().get(0).getCode(), is(ErrorCodes.TECNICO.getCode()));
  }

}
