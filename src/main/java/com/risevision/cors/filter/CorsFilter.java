package com.risevision.cors.filter;

import static com.risevision.cors.filter.Globals.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {

    private static final Pattern SEPARATOR = Pattern.compile("[\\n\\s]+");

    private Logger logger;

    private List<UrlMatcher> allowedCorsOrigins = null;
    private String allowMethods;
    private String allowCredentials;

    static List<UrlMatcher> toUrlMatcherList(String text) {
      return SEPARATOR
        .splitAsStream(text)
        .filter(pattern -> pattern.length() > 0)
        .map(UrlMatcher::create)
        .collect(Collectors.toList());
    }

    public CorsFilter(Logger logger) {
      this.logger = logger;
    }

    public CorsFilter() {
      logger = Logger.getLogger(CorsFilter.class.getName());
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
      String allowedOriginsString = config.getInitParameter(ALLOWED_ORIGINS_PARAM);

      if(allowedOriginsString != null)
        allowedCorsOrigins = toUrlMatcherList(allowedOriginsString.trim());

      allowMethods = config.getInitParameter(ALLOWED_METHODS_HEADER);
      allowCredentials = config.getInitParameter(ALLOWED_CREDENTIALS_HEADER);
    }

    private boolean isAllowedCorsOrigin(String origin) {
      return allowedCorsOrigins.stream().anyMatch(matcher -> matcher.test(origin));
    }

    private void addCorsHeaders(HttpServletResponse response, String origin) {
      if(!isAllowedCorsOrigin(origin)) {
        logger.warning(String.format("Illegal CORS origin: %s", origin));

        return;
      }

      response.setHeader(ALLOWED_ORIGIN_HEADER, origin);

      if(allowMethods != null)
        response.setHeader(ALLOWED_METHODS_HEADER, allowMethods);

      if(allowCredentials != null)
        response.setHeader(ALLOWED_CREDENTIALS_HEADER, allowCredentials);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
      logger.info("Starting cors filter execution");

      HttpServletRequest request = (HttpServletRequest) servletRequest;
      String origin = request.getHeader(ORIGIN_HEADER);

      if (origin != null) {
        if(allowedCorsOrigins != null)
          addCorsHeaders((HttpServletResponse)servletResponse, origin);
        else // original implementation, for transitional purposes
          logger.log(Level.INFO, "CORS origin: {0}", origin);
      }

      filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {}
}
