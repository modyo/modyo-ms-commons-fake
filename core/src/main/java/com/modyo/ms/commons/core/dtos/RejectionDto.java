package com.modyo.ms.commons.core.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RejectionDto extends Dto {

  private final String source;
  private final String detail;

}
