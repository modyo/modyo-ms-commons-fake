package com.modyo.ms.commons.core.filters;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@Order(1)
public class CorrelationIdFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String correlationId = UUID.randomUUID().toString();
    RequestContextHolder.currentRequestAttributes().setAttribute("correlationId", correlationId, 0);
    chain.doFilter(request, response);
  }
}
