package com.modyo.ms.commons.http.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<DatePattern, String> {

  protected String pattern;

  @Override
  public void initialize(DatePattern datePattern) {
    this.pattern = datePattern.pattern();
  }

  @Override
  public boolean isValid(String date, ConstraintValidatorContext context) {
    if (Optional.ofNullable(date).isPresent()) {
      SimpleDateFormat df = new SimpleDateFormat(pattern);
      df.setLenient(false);
      try {
        df.parse(date);
      } catch (ParseException e) {
        return false;
      }
    }
    return true;
  }
}
