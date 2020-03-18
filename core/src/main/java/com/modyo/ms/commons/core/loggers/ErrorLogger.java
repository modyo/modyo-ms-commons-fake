package com.modyo.ms.commons.core.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.dtos.Dto;
import com.modyo.ms.commons.core.dtos.ErrorsResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class ErrorLogger extends Logger {

  private String className;
  private String stackTrace;
  private ErrorsResponseDto responseBody;
  private Dto dataSourceResult;

  @Override
  public void setBasicLogInformation() {
    this.setType("exception");
    super.setBasicLogInformation();
  }
}
