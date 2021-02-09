package com.modyo.ms.commons.core.exceptions;

import com.modyo.ms.commons.core.dtos.Dto;
import lombok.Getter;

/**
 * Used when the business logic needs to throw an exception
 */
@Getter
public class CriticalBusinessErrorException extends DatasourceErrorException {

  private static final long serialVersionUID = 8283697959497165522L;

  public CriticalBusinessErrorException(String message, String messageCode) {
    super(message, messageCode);
  }

  public CriticalBusinessErrorException(String message, Dto dataSourceResult) {
    super(message, dataSourceResult);
  }
}
