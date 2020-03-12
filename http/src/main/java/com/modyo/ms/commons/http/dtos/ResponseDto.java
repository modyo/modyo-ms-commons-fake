package com.modyo.ms.commons.http.dtos;

import com.modyo.ms.commons.core.dtos.Dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseDto<T> extends Dto {

  private T data;

}
