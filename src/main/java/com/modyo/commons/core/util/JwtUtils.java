package com.modyo.commons.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modyo.commons.core.exception.CustomValidationException;
import com.modyo.commons.core.exception.dto.RejectionDto;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

public class JwtUtils {

  public static String getClaimFromAccessToken(String accessToken, String claim) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      Jwt jwt = JwtHelper.decode(accessToken.split(" ")[1]);
      Map claims = objectMapper.readValue(jwt.getClaims(), Map.class);
      String claimValue = (String) claims.get(claim);
      if (claimValue == null) {
        throw new CustomValidationException(RejectionDto.builder()
            .source(claim)
            .detail("claim " + claim + "no se encuentra dentro de header Authorization")
            .build());
      } else {
        return claimValue;
      }
    } catch (Exception e) {
      throw new CustomValidationException(RejectionDto.builder()
          .source("Authorization header")
          .detail(e.getMessage())
          .build(), e);
    }
  }

  public static Map getClaims(String accessToken) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    Jwt jwt = JwtHelper.decode(accessToken.split(" ")[1]);
    return objectMapper.readValue(jwt.getClaims(), Map.class);
  }

  public static String createJWT(HashMap<String, Object> claims) {
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    long nowMillis = System.currentTimeMillis();
    Date now = new Date(nowMillis);
    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("whatever");
    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    JwtBuilder builder = Jwts.builder()
        .setId("whateverId")
        .setIssuedAt(now)
        .setSubject("whateverSubject")
        .setIssuer("whateverIssuer")
        .setClaims(claims)
        .signWith(signatureAlgorithm, signingKey);
    long expMillis = nowMillis + 18000;
    Date exp = new Date(expMillis);
    builder.setExpiration(exp);
    return builder.compact();
  }

}
