package com.modyo.ms.commons.core.loggers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import com.modyo.ms.commons.core.InMemoryTestRequestAttributes;
import com.modyo.ms.commons.core.constants.LogTypes;
import com.modyo.ms.commons.core.dtos.Dto;
import com.modyo.ms.commons.core.dtos.ErrorsResponseDto;
import lombok.Builder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;

public class ErrorLoggerTest {

  private String testCorrelationId = "testId";

  @Before
  public void setUp() {
    RequestContextHolder.setRequestAttributes(new InMemoryTestRequestAttributes());
    RequestContextHolder.getRequestAttributes()
        .setAttribute("correlationId", testCorrelationId, 0);
  }

  @Test
  public void logInfo() {
    ErrorLogger errorLogger = ErrorLogger.builder()
        .className("className")
        .stackTrace("stackTrace")
        .responseBody(ErrorsResponseDto.builder().build())
        .dataSourceResult(TestDto.builder().build())
        .build();

    errorLogger.logInfo();

    assertThat(errorLogger.getType(), is(LogTypes.EXCEPTION));
    assertThat(errorLogger.getCorrelationId(), is(testCorrelationId));
  }

  @Test
  public void logError() {
    ErrorLogger errorLogger = ErrorLogger.builder()
        .className("className")
        .stackTrace("stackTrace")
        .build();

    errorLogger.logInfo();

    assertThat(errorLogger.getType(), is(LogTypes.EXCEPTION));
    assertThat(errorLogger.getCorrelationId(), is(testCorrelationId));
  }

  @Test
  public void builderAndSetter() {
    ErrorLogger errorLogger = ErrorLogger.builder()
        .className("className")
        .stackTrace("stackTrace")
        .responseBody(ErrorsResponseDto.builder().build())
        .dataSourceResult(TestDto.builder().build())
        .build();

    errorLogger.setClassName("new className");
    errorLogger.setStackTrace("new stacktrace");
    errorLogger.setResponseBody(ErrorsResponseDto.builder().build());
    errorLogger.setDataSourceResult(TestDto.builder().build());

    assertNotNull(errorLogger.getResponseBody().toString());
  }

  @Builder
  static class TestDto extends Dto {

  }

}
