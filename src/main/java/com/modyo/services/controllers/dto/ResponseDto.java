package com.modyo.services.controllers.dto;

import com.modyo.services.dto.Dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseDto<T> extends Dto {

  private T data;

}
