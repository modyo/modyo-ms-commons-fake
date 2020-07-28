package com.modyo.ms.commons.security.filters;

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

/**
 * Filtro para obtener el Token que proviene de Modyo
 */
@Component
@Order(1)
public class TokenFilter implements Filter {

  @Autowired
  AuthTokenBean authToken;

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
    throws IOException, ServletException {
    extractAuthToken((HttpServletRequest) request);
    chain.doFilter(request, response);
  }

  private void extractAuthToken(HttpServletRequest request) {
    String accessToken = getRequestHeaders(request).get("authorization");
    if (accessToken != null) {
      authToken.setAuthToken(accessToken);
    }
  }

  private Map<String, String> getRequestHeaders(HttpServletRequest request) {
    Map<String, String> requestHeaders = new HashMap<>();
    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = (String) headerNames.nextElement();
      requestHeaders.put(name, request.getHeader(name));
    }
    return requestHeaders;
  }
}
