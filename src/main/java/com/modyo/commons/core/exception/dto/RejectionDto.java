package com.modyo.commons.core.exception.dto;

import com.modyo.commons.core.domain.Dto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RejectionDto extends Dto {

  private final String source;
  private final String detail;

}
