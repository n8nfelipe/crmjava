package com.acme.crm.shared;

import com.acme.crm.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
    var errors =
        e.getBindingResult().getFieldErrors().stream()
            .map(x -> new ApiError.FieldError(x.getField(), x.getDefaultMessage()))
            .toList();
    return error(400, "VALIDATION_ERROR", "Dados inválidos", r, errors);
  }

  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ApiError> notFound(NotFoundException e, HttpServletRequest r) {
    return error(404, "NOT_FOUND", e.getMessage(), r, List.of());
  }

  @ExceptionHandler({BusinessException.class, DataIntegrityViolationException.class})
  ResponseEntity<ApiError> business(Exception e, HttpServletRequest r) {
    return error(
        422,
        "BUSINESS_ERROR",
        e instanceof BusinessException ? e.getMessage() : "Operação viola uma regra de integridade",
        r,
        List.of());
  }

  @ExceptionHandler(AuthenticationException.class)
  ResponseEntity<ApiError> authentication(AuthenticationException e, HttpServletRequest r) {
    return error(401, "UNAUTHORIZED", "E-mail ou senha inválidos", r, List.of());
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<ApiError> denied(AccessDeniedException e, HttpServletRequest r) {
    return error(403, "FORBIDDEN", "Acesso negado", r, List.of());
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> unknown(Exception e, HttpServletRequest r) {
    log.error("Unhandled error on {}", r.getRequestURI(), e);
    return error(500, "INTERNAL_ERROR", "Erro interno inesperado", r, List.of());
  }

  private ResponseEntity<ApiError> error(
      int status,
      String code,
      String msg,
      HttpServletRequest req,
      List<ApiError.FieldError> errors) {
    return ResponseEntity.status(status)
        .body(new ApiError(Instant.now(), status, code, msg, req.getRequestURI(), errors));
  }
}
