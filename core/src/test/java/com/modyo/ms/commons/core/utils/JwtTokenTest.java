package com.modyo.ms.commons.core.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class JwtTokenTest {

  private JwtToken validJwtToken = new JwtToken(validJwtTokenValue());

  @Test
  public void constructor_whenInvalid_ThenReturnCustomValidationException() {
    assertThrows(CustomValidationException.class, () -> new JwtToken("Bearer invalid"));
  }

  @Test
  public void getEmail() {
    assertThat(validJwtToken.getEmail(), is("prospecto@uss.com"));
  }

  @Test
  public void getFirstName() {
    assertThat(validJwtToken.getFirstName(), is("Prospecto"));
  }

  @Test
  public void getFirstName_WhenNotExist_ThenReturnBlank() {
    JwtToken jwtToken = new JwtToken("Bearer " + JwtUtils.createJWT(new HashMap<>()));

    assertThat(jwtToken.getFirstName(), is(""));
  }

  @Test
  public void getFamilyName() {
    assertThat(validJwtToken.getFamilyName(), is("USS"));
  }

  private static String validJwtTokenValue() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("given_name", "Prospecto");
    claims.put("family_name", "USS");
    claims.put("email", "prospecto@uss.com");
    claims.put("second_name", "Second");
    claims.put("second_family_name", "Secondlast");

    return "Bearer " + JwtUtils.createJWT(claims);
  }

}
