package com.modyo.ms.commons.core.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.constants.LogTypes;
import com.modyo.ms.commons.core.dtos.Dto;
import com.modyo.ms.commons.core.dtos.ErrorsResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
@Builder
@Getter
@Setter
public class ErrorLogger extends CommonsLogger {

  private String className;
  private String stackTrace;
  private ErrorsResponseDto responseBody;
  private Dto dataSourceResult;

  @Override
  public void setBasicLogInformation() {
    this.setType(LogTypes.EXCEPTION);
    super.setBasicLogInformation();
  }
}
