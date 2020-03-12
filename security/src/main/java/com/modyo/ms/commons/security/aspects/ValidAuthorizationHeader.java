package com.modyo.ms.commons.security.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AuthorizationHeaderValidator.class)
public @interface ValidAuthorizationHeader {

  String message() default "Authorization Header is not valid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
