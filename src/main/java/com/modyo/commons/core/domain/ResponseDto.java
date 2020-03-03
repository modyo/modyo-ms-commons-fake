package com.modyo.commons.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseDto<T> extends Dto {

  private T data;

}
