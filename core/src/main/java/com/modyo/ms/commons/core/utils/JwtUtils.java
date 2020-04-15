package com.modyo.ms.commons.core.utils;

import com.modyo.ms.commons.core.dtos.RejectionDto;
import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class JwtUtils {

  public static String getClaimFromAccessToken(String encodedJwt, String claim) {
    try {
      Claims claims = getClaims(encodedJwt);
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


  public static Claims getClaims(String encodedJwt) {
    return Jwts.parser()
        .setSigningKey(DatatypeConverter.parseBase64Binary(encodedJwt))
        .parseClaimsJws(encodedJwt).getBody();
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
