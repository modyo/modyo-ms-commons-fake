package com.modyo.services.exceptions.dto;

import com.modyo.services.dto.Dto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RejectionDto extends Dto {

  private final String source;
  private final String detail;

}
