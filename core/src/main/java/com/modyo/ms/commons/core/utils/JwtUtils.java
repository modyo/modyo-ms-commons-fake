package com.modyo.ms.commons.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modyo.ms.commons.core.dtos.RejectionDto;
import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

public class JwtUtils {
  public JwtUtils() {
  }

  public static String getClaimFromAccessToken(String accessToken, String claim) {
    try {
      Map<String,Object> claims = getClaims(accessToken);
      String claimValue = (String)claims.get(claim);
      if (claimValue == null) {
        throw new CustomValidationException(RejectionDto.builder().source(claim).detail("claim " + claim + "no se encuentra dentro de header Authorization").build());
      } else {
        return claimValue;
      }
    } catch (Exception var6) {
      throw new CustomValidationException(RejectionDto.builder().source("Authorization header").detail(var6.getMessage()).build(), var6);
    }
  }

  public static Map<String, Object> getClaims(String accessToken) throws IOException {
    if (accessToken == null || !accessToken.contains("Bearer ")) {
      throw new CustomValidationException(RejectionDto.builder()
          .detail("not valid")
          .source("Authorization header")
          .build());
    }
    ObjectMapper objectMapper = new ObjectMapper();
    Jwt jwt = JwtHelper.decode(accessToken.split(" ")[1]);
    return objectMapper.readValue(jwt.getClaims(), new TypeReference<Map<String,Object>>(){});
  }

  public static String createJWT(Map<String, Object> claims) {
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    long nowMillis = System.currentTimeMillis();
    Date now = new Date(nowMillis);
    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("whatever");
    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    JwtBuilder builder = Jwts.builder().setId("whateverId").setIssuedAt(now).setSubject("whateverSubject").setIssuer("whateverIssuer").setClaims(claims).signWith(signatureAlgorithm, signingKey);
    long expMillis = nowMillis + 18000L;
    Date exp = new Date(expMillis);
    builder.setExpiration(exp);
    return builder.compact();
  }
}
