package com.risevision.cors.filter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Matchers.*;

import java.util.logging.Logger;
import java.util.logging.Level;


public class CorsFilterTest {
  @Mock private Logger logger;  
  @Mock private HttpServletRequest httpServletRequest;
  @Mock private HttpServletResponse httpServletResponse;
  @Mock private FilterChain filterChain;

  private final String ORIGIN = "origin";

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void logsAndCallsNextFilter() throws IOException, ServletException {
    given(httpServletRequest.getHeader(ORIGIN)).willReturn("test.example.com");
    CorsFilter filter = new CorsFilter(logger);
    filter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    verify(logger, atLeastOnce()).log(any(Level.class), anyString(), anyString());
    verify(filterChain).doFilter(anyObject(), anyObject());
  }
}
