package com.modyo.ms.commons.core.exceptions;

import com.modyo.ms.commons.core.dtos.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DatasourceErrorException extends RuntimeException {

  private static final long serialVersionUID = 8283697959497165526L;
  private String messageCode;
  private Dto dataSourceResult;

  public DatasourceErrorException(String message) {
    super(message);
  }

  public DatasourceErrorException(String message, String messageCode) {
    super(message);
    this.setMessageCode(messageCode);
  }

  public DatasourceErrorException(String message, Throwable cause) {
    super(message, cause);
  }

  public DatasourceErrorException(String message, Throwable cause, Dto dataSourceResult) {
    super(message, cause);
    this.dataSourceResult = dataSourceResult;
  }

  public DatasourceErrorException(String message, Dto dataSourceResult) {
    super(message);
    this.dataSourceResult = dataSourceResult;
  }

}
