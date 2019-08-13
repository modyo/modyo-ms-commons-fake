package com.modyo.services.filters.dto;

import com.modyo.services.dto.LogDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class RequestLogDto extends LogDto {

  private String method;
  private String uri;
  private Map<String, String> headers;
  private Map<String, String> parameters;

  @Override
  public void setBasicLogInformation() {
    this.setType("request");
    super.setBasicLogInformation();
  }

}
