package com.modyo.ms.commons.core.constants;

import lombok.Getter;

/**
 * Listado de Códigos de Error Interno
 */

@Getter
public class LogTypes {

  public static final String REQUEST = "request";
  public static final String RESPONSE = "response";
  public static final String EXCEPTION = "exception";

  private LogTypes() {}

}
