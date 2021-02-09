package com.modyo.ms.commons.core.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Dto que contiene detalle de un error. Parámetros definidos bajo el Estándar JSON API v1.0. Para
 * más información, visitar https://jsonapi.org/format/#error-objects.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ErrorDto extends Dto {

  /**
   * Identificador único para esta ocurrencia particular del error
   */
  private Integer id;

  /**
   * HTTP status code aplicable a este error
   */
  @NotNull
  private String status;

  /**
   * Código de aplicación que identifica el tipo de error
   */
  @NotNull
  private String code;

  /**
   * Resumen para este tipo de error
   */
  @NotNull
  private String title;

  /**
   * Código de mensaje que identifica el mensaje de error
   */
  private String messageCode;

  /**
   * Objeto que contiene referencias de la fuente del error
   */
  private String source;

  /**
   * Resumen para este tipo de error
   */
  private String detail;
}
