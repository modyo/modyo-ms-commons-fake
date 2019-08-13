package com.modyo.services.configuration.dto;

import lombok.Data;

@Data
public class CustomAwsDto {
  private String httpMethod;
  private String contextUri;
  private String tipoBinario;
}
