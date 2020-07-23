package com.modyo.ms.commons.core.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import org.junit.Test;

public class RutUtilsTest {

  @Test(expected = CustomValidationException.class)
  public void givenRutHasLetters_ThenThrowCustomValidationException() {
    new RutUtils("7608642s-5", true);
  }

  @Test(expected = CustomValidationException.class)
  public void givenDvIsIncorrect_ThenThrowCustomValidationException() {
    new RutUtils("76086428-4", true);
  }

  @Test(expected = CustomValidationException.class)
  public void givenRutHasTooManyDigits_ThenThrowCustomValidationException() {
    new RutUtils("760864281-5", false);
  }

  @Test(expected = CustomValidationException.class)
  public void givenDvNotIncludedButIncludedInString_ThenThrowCustomValidationException() {
    new RutUtils("76086428-5", false);
  }

  @Test
  public void unformatted_givenIncludedDv() {
    RutUtils rutUtils = new RutUtils("76086428-5", true);

    String unformatted = rutUtils.unformatted();

    assertThat(unformatted, is("760864285"));
  }

  @Test
  public void unformatted_givenDvNotIncluded() {
    RutUtils rutUtils = new RutUtils("76086428", false);

    String unformatted = rutUtils.unformatted();

    assertThat(unformatted, is("760864285"));

  }

  @Test
  public void formattedWithoutPoints() {
    RutUtils rutUtils = new RutUtils("76.086.428", false);

    String unformatted = rutUtils.formattedWithoutPoints();

    assertThat(unformatted, is("76086428-5"));
  }

  @Test
  public void formattedWithPoints() {
    RutUtils rutUtils = new RutUtils("76.086.428-5", true);

    String unformatted = rutUtils.formattedWithPoints();

    assertThat(unformatted, is("76.086.428-5"));
  }

  @Test
  public void greaterThan50000000_ThenIsJuridico() {
    RutUtils rutUtils = new RutUtils("76.086.428-5", true);

    assertTrue(rutUtils.isJuridico());
    assertFalse(rutUtils.isNatural());
  }

  @Test
  public void equalsThan50000000_ThenIsJuridico() {
    RutUtils rutUtils = new RutUtils("50.000.000", false);

    assertTrue(rutUtils.isJuridico());
    assertFalse(rutUtils.isNatural());
  }

  @Test
  public void smallerThan50000000_ThenIsNatural() {
    RutUtils rutUtils = new RutUtils("49.999.999", false);

    assertFalse(rutUtils.isJuridico());
    assertTrue(rutUtils.isNatural());
  }


}
