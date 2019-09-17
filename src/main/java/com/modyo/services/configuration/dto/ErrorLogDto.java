package com.modyo.services.configuration.dto;

import com.modyo.services.dto.Dto;
import com.modyo.services.dto.LogDto;
import com.modyo.services.exceptions.dto.ErrorsResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class ErrorLogDto extends LogDto {

  private String className;
  private Exception exception;
  private ErrorsResponseDto responseBody;
  private Dto dataSourceResult;

  @Override
  public void setBasicLogInformation() {
    this.setType("exception");
    super.setBasicLogInformation();
  }

}
