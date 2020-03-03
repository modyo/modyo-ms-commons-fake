package com.modyo.commons.core.exception;

import com.modyo.commons.core.domain.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatasourceErrorException extends RuntimeException {

  private static final long serialVersionUID = 8283697959497165526L;
  private Dto dataSourceResult;

  public DatasourceErrorException(String message) {
    super(message);
  }

  public DatasourceErrorException(String message, Throwable cause) {
    super(message, cause);
  }

}
