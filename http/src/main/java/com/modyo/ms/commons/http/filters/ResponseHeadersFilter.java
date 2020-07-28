package com.modyo.ms.commons.http.filters;

import com.modyo.ms.commons.http.constants.CustomHttpHeaders;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Filtro para Transacciones HTTP
 */
@Component
@Order(3)
public class ResponseHeadersFilter implements Filter {

  @Value("${spring.application.name}")
  private String applicationName;

  @Override
  public void doFilter(ServletRequest request,ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {
    addResponseHeaders((HttpServletRequest) request, (HttpServletResponse) response);
    chain.doFilter(request, response);
  }

  private void addResponseHeaders(HttpServletRequest request, HttpServletResponse response) {
    String correlationId = Objects.requireNonNullElse(
        RequestContextHolder.currentRequestAttributes().getAttribute("correlationId", 0)
        , "").toString();
    response.addHeader(CustomHttpHeaders.CORRELATION_ID, correlationId);
    if (applicationName != null) {
      response.addHeader(CustomHttpHeaders.APPLICATION_NAME, applicationName);
    }
  }
}
