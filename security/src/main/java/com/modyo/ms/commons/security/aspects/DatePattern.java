package com.modyo.services.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface DatePattern {

  String message() default "not match with date pattern {pattern}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String pattern();

  @Target({ ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @interface List {
    DatePattern[] pattern();
  }

}
