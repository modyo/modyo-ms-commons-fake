package com.modyo.services.configuration;

import com.modyo.services.utils.JwtUtils;
import java.io.IOException;
import java.util.Map;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthTokenBean {

  private String authToken;

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public Map listClaims() throws IOException {
    return JwtUtils.getClaims(this.authToken);
  }

  public String getClaim(String claim) {
    return JwtUtils.getClaimFromAccessToken(this.authToken, claim);
  }
}
