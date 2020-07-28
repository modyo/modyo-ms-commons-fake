package com.modyo.ms.commons.core.dtos;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RejectionDto extends Dto {

  private final String source;
  private final String detail;

}
