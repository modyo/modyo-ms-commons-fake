package com.modyo.ms.commons.security.aspects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuthorizationHeaderValidator implements
    ConstraintValidator<ValidAuthorizationHeader, String> {

  @Override
  public boolean isValid(String authorizationHeader, ConstraintValidatorContext context) {
    //TODO extender funcionalidad para que sea capaz de validar Authorization Headers de diferente tipo (Bearer, Basic, etc)
    return authorizationHeader != null &&
        !authorizationHeader.isEmpty() &&
        authorizationHeader.length() > 7 &&
        authorizationHeader.substring(0, 7).equals("Bearer ");
    //TODO validar que el contenido sea un JWT codificado en base64
  }
}
