package com.modyo.ms.commons.awsapigw.dtos;

import lombok.Data;

@Data
public class CustomAwsDto {
  private String httpMethod;
  private String contextUri;
  private String tipoBinario;
}
