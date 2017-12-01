package com.mc.driveweek.mcfitbit;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

public class SimpleAuthFilter extends GenericFilterBean {

  private final String key;

  SimpleAuthFilter(String key) {
    this.key = key;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    String key = servletRequest.getParameter("key");
    if(key == null || !this.key.equals(key)){
      ((HttpServletResponse) servletResponse).setStatus(HttpStatus.UNAUTHORIZED.value());
      servletResponse.getWriter().write("Invalid or missing key");
      return;
    }

    filterChain.doFilter(servletRequest, servletResponse);
  }
}
