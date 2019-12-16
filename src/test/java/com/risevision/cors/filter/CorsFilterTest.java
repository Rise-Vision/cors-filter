package com.risevision.cors.filter;

import static com.risevision.cors.filter.Globals.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Matchers.*;

import java.util.logging.Logger;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

public class CorsFilterTest {

  private static final Predicate<UrlMatcher> MATCHES_APPS =
    matcher -> matcher.test("https://apps.risevision.com");
  private static final Predicate<UrlMatcher> MATCHES_APPS_STAGE =
    matcher -> matcher.test("https://apps-stage-7.risevision.com");
  private static final Predicate<UrlMatcher> MATCHES_RVA_USER =
    matcher -> matcher.test("https://rvauser.risevision.com");
  private static final Predicate<UrlMatcher> MATCHES_RVA_USER2 =
    matcher -> matcher.test("https://rvauser2.appspot.com");
  private static final Predicate<UrlMatcher> MATCHES_RVA_USER2_TEST =
    matcher -> matcher.test("http://1-07-021.rvauser2.appspot.com");
  private static final Predicate<UrlMatcher> MATCHES_RVA_USER2_STAGE =
    matcher -> matcher.test("http://in-app-test-dot-rvauser2.appspot.com");
  private static final Predicate<UrlMatcher> MATCHES_OTHER =
    matcher -> matcher.test("https://www.apache.org");

  @Mock private Logger logger;  
  @Mock private HttpServletRequest httpServletRequest;
  @Mock private HttpServletResponse httpServletResponse;
  @Mock private FilterChain filterChain;
  @Mock private FilterConfig filterConfig;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createsSimpleUrlMatcher() {
    String param = "\n   apps.risevision.com\n    ";
    List<UrlMatcher> origins = CorsFilter.toUrlMatcherList(param);

    assertEquals(1, origins.size());
    assertTrue(origins.stream().anyMatch(MATCHES_APPS));
    assertFalse(origins.stream().anyMatch(MATCHES_APPS_STAGE));
    assertFalse(origins.stream().anyMatch(MATCHES_RVA_USER));
    assertFalse(origins.stream().anyMatch(MATCHES_RVA_USER2_TEST));
    assertFalse(origins.stream().anyMatch(MATCHES_RVA_USER2_STAGE));
    assertFalse(origins.stream().anyMatch(MATCHES_RVA_USER2));
    assertFalse(origins.stream().anyMatch(MATCHES_OTHER));
  }

  @Test
  public void createsMultipleUrlMatcher() {
    String param = "\n   *.risevision.com\n   *rvauser.appspot.com\n   *rvauser2.appspot.com\n    ";
    List<UrlMatcher> origins = CorsFilter.toUrlMatcherList(param);

    assertEquals(3, origins.size());
    assertTrue(origins.stream().anyMatch(MATCHES_APPS));
    assertTrue(origins.stream().anyMatch(MATCHES_APPS_STAGE));
    assertTrue(origins.stream().anyMatch(MATCHES_RVA_USER));
    assertTrue(origins.stream().anyMatch(MATCHES_RVA_USER2_TEST));
    assertTrue(origins.stream().anyMatch(MATCHES_RVA_USER2_STAGE));
    assertTrue(origins.stream().anyMatch(MATCHES_RVA_USER2));
    assertFalse(origins.stream().anyMatch(MATCHES_OTHER));
  }

  @Test
  public void logsAndCallsNextFilter() throws IOException, ServletException {
    given(httpServletRequest.getHeader(ORIGIN_HEADER)).willReturn("test.example.com");
    CorsFilter filter = new CorsFilter(logger);
    filter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    verify(logger, atLeastOnce()).log(any(Level.class), anyString(), anyString());
    verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    verifyZeroInteractions(httpServletResponse);
  }

  @Test
  public void addsCorsOriginToHttpCall() throws IOException, ServletException {
    String origin = "http://apps.risevision.com";

    given(httpServletRequest.getHeader(ORIGIN_HEADER)).willReturn(origin);
    given(filterConfig.getInitParameter(ALLOWED_ORIGINS_PARAM)).willReturn("apps.risevision.com");

    CorsFilter filter = new CorsFilter(logger);
    filter.init(filterConfig);
    filter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    verify(logger, never()).log(any(Level.class), anyString(), anyString());
    verify(filterChain).doFilter(httpServletRequest, httpServletResponse);

    verify(httpServletResponse, times(1)).setHeader(ALLOWED_ORIGIN_HEADER, origin);
    verify(httpServletResponse, never()).setHeader(eq(ALLOWED_METHODS_HEADER), anyString());
    verify(httpServletResponse, never()).setHeader(eq(ALLOWED_CREDENTIALS_HEADER), anyString());
  }

  @Test
  public void addsCorsOriginToHttpsCall() throws IOException, ServletException {
    String origin = "https://apps.risevision.com";

    given(httpServletRequest.getHeader(ORIGIN_HEADER)).willReturn(origin);
    given(filterConfig.getInitParameter(ALLOWED_ORIGINS_PARAM)).willReturn("apps.risevision.com");

    CorsFilter filter = new CorsFilter(logger);
    filter.init(filterConfig);
    filter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    verify(logger, never()).log(any(Level.class), anyString(), anyString());
    verify(filterChain).doFilter(httpServletRequest, httpServletResponse);

    verify(httpServletResponse, times(1)).setHeader(ALLOWED_ORIGIN_HEADER, origin);
    verify(httpServletResponse, never()).setHeader(eq(ALLOWED_METHODS_HEADER), anyString());
    verify(httpServletResponse, never()).setHeader(eq(ALLOWED_CREDENTIALS_HEADER), anyString());
  }

  @Test
  public void doesntAddCorsOriginToUnsupportedOrigin() throws IOException, ServletException {
    String origin = "http://apps.piratevision.com";

    given(httpServletRequest.getHeader(ORIGIN_HEADER)).willReturn(origin);
    given(filterConfig.getInitParameter(ALLOWED_ORIGINS_PARAM)).willReturn("apps.risevision.com");

    CorsFilter filter = new CorsFilter(logger);
    filter.init(filterConfig);
    filter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    verify(logger, never()).log(any(Level.class), anyString(), anyString());
    verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    verify(logger, times(1)).warning(anyString());
    verifyZeroInteractions(httpServletResponse);
  }

  @Test
  public void addsCorsMethodsHeaderIfConfigured() throws IOException, ServletException {
    String origin = "https://apps.risevision.com";

    given(httpServletRequest.getHeader(ORIGIN_HEADER)).willReturn(origin);
    given(filterConfig.getInitParameter(ALLOWED_ORIGINS_PARAM)).willReturn("apps.risevision.com");
    given(filterConfig.getInitParameter(ALLOWED_METHODS_HEADER)).willReturn("GET");

    CorsFilter filter = new CorsFilter(logger);
    filter.init(filterConfig);
    filter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    verify(logger, never()).log(any(Level.class), anyString(), anyString());
    verify(filterChain).doFilter(httpServletRequest, httpServletResponse);

    verify(httpServletResponse, times(1)).setHeader(ALLOWED_ORIGIN_HEADER, origin);
    verify(httpServletResponse, times(1)).setHeader(ALLOWED_METHODS_HEADER, "GET");
    verify(httpServletResponse, never()).setHeader(eq(ALLOWED_CREDENTIALS_HEADER), anyString());
  }

  @Test
  public void addsCorsCredentialsHeaderIfConfigured() throws IOException, ServletException {
    String origin = "https://apps.risevision.com";

    given(httpServletRequest.getHeader(ORIGIN_HEADER)).willReturn(origin);
    given(filterConfig.getInitParameter(ALLOWED_ORIGINS_PARAM)).willReturn("apps.risevision.com");
    given(filterConfig.getInitParameter(ALLOWED_CREDENTIALS_HEADER)).willReturn("true");

    CorsFilter filter = new CorsFilter(logger);
    filter.init(filterConfig);
    filter.doFilter(httpServletRequest, httpServletResponse, filterChain);

    verify(logger, never()).log(any(Level.class), anyString(), anyString());
    verify(filterChain).doFilter(httpServletRequest, httpServletResponse);

    verify(httpServletResponse, times(1)).setHeader(ALLOWED_ORIGIN_HEADER, origin);
    verify(httpServletResponse, never()).setHeader(eq(ALLOWED_METHODS_HEADER), anyString());
    verify(httpServletResponse, times(1)).setHeader(ALLOWED_CREDENTIALS_HEADER, "true");
  }

}
