package com.modyo.ms.commons.core.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Internal Error Codes List
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCodes {

  INTERNAL("1000", "Internal error"),
  INVALID_PARAM("1001", "Invalid param"),
  METHOD_NOT_ALLOWED("1002", "Method not allowed"),
  UNSUPPORTED_MEDIA_TYPE("1003", "Unsupported media type"),
  MALFORMED_REQUEST("1004", "Malformed request"),
  EXTERNAL_SERVICE("1005", "External service error"),
  MALFORMED_EXTERNAL_SERVICE_REQUEST("1006", "Malformed external service request"),
  UNKNOWN_EXTERNAL_SERVICE_ERROR("1007", "Unknown external service error"),
  EXTERNAL_SERVICE_REQUEST_ERROR("1008", "External service request error"),
  MISSING_REQUEST_HEADER("1009", "Missing request header"),
  BUSINESS_ERROR("1010", "Business error"),
  MISSING_PARAM("1011", "Missing param"),
  ACCESS_FORBIDDEN("1012", "Access forbidden"),
  TECHNICAL_ERROR("1013", "Technical error"),
  RESOURCE_NOT_FOUND("1014", "Resource not found"),
  CRITICAL_BUSINESS_ERROR("1015", "Critical Business error"),
  @Deprecated INTERNO("1000", "Ha ocurrido un error interno"),
  @Deprecated PARAMETRO_NO_VALIDO("1001", "Parámetro inválido"),
  @Deprecated METODO_NO_SOPORTADO("1002", "Metodo no soportado"),
  @Deprecated FORMATO_NO_SOPORTADO("1003", "Formato no soportado"),
  @Deprecated OBJETO_MAL_FORMADO("1004", "Objeto mal formado"),
  @Deprecated SERVICIO_EXTERNO("1005", "Ha ocurrido un error con un servicio externo"),
  @Deprecated CONSULTA_SERVICIO_MAL_FORMADA("1006", "Consulta mal formada a un servicio externo"),
  @Deprecated SERVICIO_EXTERNO_DESCONOCIDO("1007", "Error de servicio externo desconocido"),
  @Deprecated CONSULTA_SERVICIO_EXTERNO("1008", "Error al consultar servicio externo"),
  @Deprecated HEADER_FALTANTE("1009", "Header requerido no viene en la consulta"),
  @Deprecated NEGOCIO("1010", "Ha ocurrido un error de negocio"),
  @Deprecated PARAMETRO_FALTANTE("1011", "Parámetro requerido no viene en la consulta"),
  @Deprecated ACCESO_PROHIBIDO("1012", "Acceso Prohibido"),
  @Deprecated TECNICO("1013", "Ha ocurrido un error técnico");

  private final String code;
  private final String message;

}
