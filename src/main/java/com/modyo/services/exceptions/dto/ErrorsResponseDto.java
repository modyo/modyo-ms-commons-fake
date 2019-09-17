package com.modyo.services.exceptions.dto;

import com.modyo.services.dto.Dto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * Objeto que contiene lista de error json-api
 */
@Data
@Builder
public class ErrorsResponseDto extends Dto {

  /**
   * Listado de errores
   */
  @Valid
  @Size(min = 1)
  @Singular
  private List<ErrorDto> errors;
}
