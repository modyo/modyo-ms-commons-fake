package com.modyo.pending.filters;

import com.modyo.services.configuration.AuthTokenBean;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Component;

/**
 * Filtro para obtener el Token que proviene de Modyo
 */
@Component
@Order(2)
public class TokenFilter implements Filter {

  @Autowired
  AuthTokenBean authToken;

  private HttpServletRequest request;

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
    throws IOException, ServletException {
    this.request = (HttpServletRequest) request;
    extractAuthToken();
    chain.doFilter(request, response);
  }

  private void extractAuthToken() {
    Map<String, String> headers = getRequestHeaders();
    String accessToken = headers.get("authorization");
    if (accessToken != null) {
      authToken.setAuthToken(accessToken);
    }
  }

  private Map<String, String> getRequestHeaders() {
    Map<String, String> map = new HashMap<>();
    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = (String) headerNames.nextElement();
      map.put(name, request.getHeader(name));
    }
    return map;
  }
}
