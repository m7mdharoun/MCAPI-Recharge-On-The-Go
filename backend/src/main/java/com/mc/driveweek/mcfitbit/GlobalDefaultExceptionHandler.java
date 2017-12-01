package com.mc.driveweek.mcfitbit;

import java.util.Collections;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalDefaultExceptionHandler  {

  @ExceptionHandler(SecurityException.class)
  protected ResponseEntity<Object> handleSecurityException(Exception ex, WebRequest request) {
    return ResponseEntity.status(401).body(Collections.singletonMap("error", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, WebRequest request) {
      return ResponseEntity.status(500).body(Collections.singletonMap("error", ex.getMessage()));
    }
}