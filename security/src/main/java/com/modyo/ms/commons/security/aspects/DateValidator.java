package com.modyo.services.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    df.setLenient(false);
    try {
      df.parse(date);
    } catch (ParseException e) {
      return false;
    }
    return true;
  }
}
