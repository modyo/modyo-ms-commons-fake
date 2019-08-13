package com.modyo.services.exceptions.dto;

import com.modyo.services.dto.Dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Dto que contiene detalle de un error. Parámetros definidos bajo el Estándar JSON API v1.0. Para
 * más información, visitar https://jsonapi.org/format/#error-objects.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
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
   * Objeto que contiene referencias de la fuente del error
   */
  private String source;

  /**
   * Resumen para este tipo de error
   */
  private String detail;
}
