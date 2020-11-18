package com.modyo.ms.commons.core.utils;

import com.modyo.ms.commons.core.dtos.RejectionDto;
import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import lombok.Value;

@Value
public class JwtToken {

  String value;
  Map<String,Object> claims;

  public JwtToken(String token) {
    this.value = token;
    try {
      claims = JwtUtils.getClaims(this.value);
    } catch (IllegalArgumentException | IOException e) {
      throw new CustomValidationException(new RejectionDto("JWT Token", "cannot extract claims"));
    }
  }

  public String getEmail() {
    return (String) claims.get("email");
  }

  public String getFirstName() {
    return (String) claims.getOrDefault("given_name", "");
  }

  public String getFamilyName() {
    return (String) claims.getOrDefault("family_name", "");
  }

  public Map<String,Object> getClaims() {
    return Collections.unmodifiableMap(claims);
  }

}
