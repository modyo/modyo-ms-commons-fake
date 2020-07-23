package com.modyo.ms.commons.core.filters;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.then;

import com.modyo.ms.commons.core.InMemoryTestRequestAttributes;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;

public class CorrelationIdFilterTest {

  @Before
  public void setUp() {
    RequestContextHolder.setRequestAttributes(new InMemoryTestRequestAttributes());
  }

  @Test
  public void doFilter() throws IOException, ServletException {
    FilterChain filterChain = Mockito.mock(FilterChain.class);
    ServletRequest servletRequest = Mockito.mock(ServletRequest.class);
    ServletResponse servletResponse = Mockito.mock(ServletResponse.class);
    CorrelationIdFilter correlationIdFilter = new CorrelationIdFilter();

    correlationIdFilter.doFilter(servletRequest, servletResponse, filterChain);

    assertNotNull(RequestContextHolder.getRequestAttributes().getAttribute("correlationId", 0));
    then(filterChain).should()
        .doFilter(servletRequest, servletResponse);
  }

}
