package com.risevision.cors.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

import java.util.logging.Logger;
import java.util.logging.Level;

public class CorsFilter implements Filter {

    private Logger logger;

    private final String ORIGIN = "origin";

    public CorsFilter(Logger logger) {
      this.logger = logger;
    }

    public CorsFilter() {
      logger = Logger.getLogger(CorsFilter.class.getName());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
      logger.info("Starting cors filter execution");

      HttpServletRequest request = (HttpServletRequest) servletRequest;
      String origin = request.getHeader(ORIGIN);

      if (origin != null) {
        logger.log(Level.INFO, "CORS origin: {0}", origin);
      }

      filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {}
}
