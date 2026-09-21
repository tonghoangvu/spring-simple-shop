package io.github.tonghoangvu.springsimpleshop.backofficeservice.component;

import io.github.tonghoangvu.springsimpleshop.backofficeservice.model.enums.ErrorCode;
import io.github.tonghoangvu.springsimpleshop.backofficeservice.model.exception.ApplicationException;
import io.github.tonghoangvu.springsimpleshop.backofficeservice.util.ValidationUtils;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.OffsetDateTime;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ApiResponse(
      responseCode = "500",
      description = "Internal Server Error",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))
  public ResponseEntity<Object> handleUnexpectedException(Exception ex, WebRequest request) {
    if (logger.isErrorEnabled()) {
      logger.error("Unexpected exception: " + ex.getMessage(), ex);
    }
    ProblemDetail body = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    return handleExceptionInternal(
        ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
    if (logger.isDebugEnabled()) {
      logger.debug("Access denied exception: " + ex.getMessage(), ex);
    }
    ProblemDetail body = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Object> handleAuthenticationException(
      AuthenticationException ex, WebRequest request) {
    if (logger.isDebugEnabled()) {
      logger.debug("Authentication exception: " + ex.getMessage(), ex);
    }
    ProblemDetail body = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<Object> handleApplicationException(
      ApplicationException ex, WebRequest request) {
    if (logger.isDebugEnabled()) {
      logger.debug("Application exception: " + ex.getMessage()); // no need to log stack trace
    }
    return handleExceptionInternal(ex, null, ex.getHeaders(), ex.getStatusCode(), request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ProblemDetail body = ex.updateAndGetBody(getMessageSource(), LocaleContextHolder.getLocale());
    body.setProperty("errors", ValidationUtils.extractErrorMessages(ex));
    return handleExceptionInternal(ex, body, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ProblemDetail body = ex.updateAndGetBody(getMessageSource(), LocaleContextHolder.getLocale());
    body.setProperty("errors", ValidationUtils.extractErrorMessages(ex));
    return handleExceptionInternal(ex, body, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> createResponseEntity(
      Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
    if (body instanceof ProblemDetail problemDetail) {
      enrichResponseBody(problemDetail, statusCode);
    } else if (body != null && logger.isWarnEnabled()) {
      logger.warn("Response body type is not ProblemDetail: " + body.getClass().getName());
    }
    return new ResponseEntity<>(body, headers, statusCode);
  }

  private void enrichResponseBody(ProblemDetail body, HttpStatusCode statusCode) {
    if (body.getProperties() == null || !body.getProperties().containsKey("code")) {
      body.setProperty("code", ErrorCode.resolve(statusCode).getCode());
    }
    body.setProperty("timestamp", OffsetDateTime.now());
  }
}
