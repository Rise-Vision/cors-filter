package com.risevision.cors.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UrlMatcherTest {

  @Test
  public void testHttpMatchOnFixedOrigin() {
    UrlMatcher matcher = UrlMatcher.create("rvauser.appspot.com");

    assertTrue(matcher.test("http://rvauser.appspot.com"));
  }

  @Test
  public void testHttpsMatchOnFixedOrigin() {
    UrlMatcher matcher = UrlMatcher.create("rvauser.appspot.com");

    assertTrue(matcher.test("https://rvauser.appspot.com"));
  }

  @Test
  public void testNoMatchOnFixedOrigin() {
    UrlMatcher matcher = UrlMatcher.create("rvauser.appspot.com");

    assertFalse(matcher.test("http://rvauser-appspot.com"));
    assertFalse(matcher.test("httpk://rvauser.appspot.com"));
    assertFalse(matcher.test("http://rvauser2.appspot.com"));
    assertFalse(matcher.test("http://www.appspot.com"));
  }

  @Test
  public void testDynamicHttpMatch() {
    UrlMatcher matcher = UrlMatcher.create("*.risevision.com");

    assertTrue(matcher.test("http://apps.risevision.com"));
    assertTrue(matcher.test("http://rva.risevision.com"));
    assertTrue(matcher.test("http://apps-stage-7.risevision.com"));
    assertTrue(matcher.test("http://store-stage-0.risevision.com"));
  }

  @Test
  public void testDynamicHttpsMatch() {
    UrlMatcher matcher = UrlMatcher.create("*.risevision.com");

    assertTrue(matcher.test("https://apps.risevision.com"));
    assertTrue(matcher.test("https://rva.risevision.com"));
    assertTrue(matcher.test("https://apps-stage-7.risevision.com"));
    assertTrue(matcher.test("https://store-stage-0.risevision.com"));
  }

  @Test
  public void testDynamicNoMatch() {
    UrlMatcher matcher = UrlMatcher.create("*.risevision.com");

    assertFalse(matcher.test("http//apps.risevision.com"));
    assertFalse(matcher.test("http://rvarisevision.com"));
    assertFalse(matcher.test("http://apps#stage-7.risevision.com"));
    assertFalse(matcher.test("file://store-stage-0.risevision.com"));
  }

  @Test
  public void testDynamicRvaUser2HttpMatch() {
    UrlMatcher matcher = UrlMatcher.create("*rvauser2.appspot.com");

    assertTrue(matcher.test("http://rvauser2.appspot.com"));
    assertTrue(matcher.test("http://1-07-021.rvauser2.appspot.com"));
    assertTrue(matcher.test("http://in-app-test-dot-rvauser2.appspot.com"));
  }

  @Test
  public void testDynamicRvaUser2HttpsMatch() {
    UrlMatcher matcher = UrlMatcher.create("*rvauser2.appspot.com");

    assertTrue(matcher.test("https://rvauser2.appspot.com"));
    assertTrue(matcher.test("https://1-07-021.rvauser2.appspot.com"));
    assertTrue(matcher.test("https://in-app-test-dot-rvauser2.appspot.com"));
  }

}
