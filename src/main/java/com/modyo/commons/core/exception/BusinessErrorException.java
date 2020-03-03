package com.modyo.commons.core.exception;

import com.modyo.commons.core.domain.Dto;
import lombok.Getter;

/**
 * Excepción para manejar errores de negocio.
 */
@Getter
public class BusinessErrorException extends DatasourceErrorException {

  private static final long serialVersionUID = 8283697959497165522L;

  public BusinessErrorException(String message, Dto dataSourceResult) {
    super(message);
    this.setDataSourceResult(dataSourceResult);
  }
}
