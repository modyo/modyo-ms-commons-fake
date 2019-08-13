package com.modyo.services.exceptions;

import com.modyo.services.dto.Dto;
import lombok.Getter;

/**
 * Excepción para manejar errores técnicos en tiempos de ejecución.
 */
@Getter
public class TechnicalErrorException extends DatasourceErrorException {

  private static final long serialVersionUID = 8283697959497165525L;

  public TechnicalErrorException(String message, Throwable cause) {
    super(message, cause);
  }

  public TechnicalErrorException(String message, Dto dataSourceResult) {
    super(message);
    this.setDataSourceResult(dataSourceResult);
  }

  public TechnicalErrorException(String message, Throwable cause, Dto dataSourceResult) {
    super(message, cause);
    this.setDataSourceResult(dataSourceResult);
  }
}
