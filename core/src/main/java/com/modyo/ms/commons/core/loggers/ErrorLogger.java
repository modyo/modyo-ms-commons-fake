package com.modyo.ms.commons.core.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modyo.ms.commons.core.dtos.Dto;
import com.modyo.ms.commons.core.dtos.ErrorsResponseDto;
import com.modyo.ms.commons.core.exceptions.TechnicalErrorException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class ErrorLogger extends Logger {

  private String className;
  private Exception exception;
  private ErrorsResponseDto responseBody;
  private Dto dataSourceResult;

  @Override
  public void logInfo() {
    verifyIfExceptionIsSerializable();
    super.logInfo();
  }

  @Override
  public void logError() {
    verifyIfExceptionIsSerializable();
    super.logError();
  }

  @Override
  public void setBasicLogInformation() {
    this.setType("exception");
    super.setBasicLogInformation();
  }

  private void verifyIfExceptionIsSerializable() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      exception = new TechnicalErrorException(
          "No es posible serializar excepción original en formato JSON", e);
    }
  }

}
