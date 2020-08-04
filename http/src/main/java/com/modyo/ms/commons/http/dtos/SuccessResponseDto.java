package com.modyo.ms.commons.http.dtos;

public interface SuccessResponseDto<D> {

  D getData();
  void setData(D data);

}
