package com.modyo.services.exceptions;

import com.modyo.services.exceptions.dto.RejectionDto;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * Excepcion para manejar errores de validación de datos de entradas que no pueden ser manejados con
 * Bean Validation
 */

@Getter
public class CustomValidationException extends RuntimeException {

  private static final long serialVersionUID = 8283697959497165523L;
  private final List<RejectionDto> rejections;

  public CustomValidationException(List<RejectionDto> rejections) {
    super();
    this.rejections = rejections;
  }

  public CustomValidationException(List<RejectionDto> rejections, Throwable cause) {
    super(cause);
    this.rejections = rejections;
  }

  public CustomValidationException(RejectionDto rejection) {
    this(Collections.singletonList(rejection));
  }

  public CustomValidationException(RejectionDto rejection, Throwable cause) {
    this(Collections.singletonList(rejection), cause);
  }

}
