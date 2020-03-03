package com.modyo.commons.core.constant;

import lombok.Getter;

/**
 * Listado de Códigos de Error Interno
 */

@Getter
public enum ErrorCodes {

  INTERNO("1000", "Ha ocurrido un error interno"),
  PARAMETRO_NO_VALIDO("1001", "Parámetro inválido"),
  METODO_NO_SOPORTADO("1002", "Metodo no soportado"),
  FORMATO_NO_SOPORTADO("1003", "Formato no soportado"),
  OBJETO_MAL_FORMADO("1004", "Objeto mal formado"),
  SERVICIO_EXTERNO("1005", "Ha ocurrido un error con un servicio externo"),
  CONSULTA_SERVICIO_MAL_FORMADA("1006", "Consulta mal formada a un servicio externo"),
  SERVICIO_EXTERNO_DESCONOCIDO("1007", "Error de servicio externo desconocido"),
  CONSULTA_SERVICIO_EXTERNO("1008", "Error al consultar servicio externo"),
  HEADER_FALTANTE("1009", "Header requerido no viene en la consulta"),
  NEGOCIO("1010", "Ha ocurrido un error de negocio"),
  PARAMETRO_FALTANTE("1011", "Parámetro requerido no viene en la consulta"),
  ACCESO_PROHIBIDO("1012", "Acceso Prohibido"),
  TECNICO("1013", "Ha ocurrido un error técnico");

  private final String code;
  private final String message;

  ErrorCodes(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
