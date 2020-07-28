package com.modyo.ms.commons.http.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.modyo.ms.commons.core.dtos.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class SuccessResponseDto extends Dto {

  private final Dto data;

  @JsonInclude(Include.NON_NULL)
  private Dto meta;

}
