package io.github.tonghoangvu.springsimpleshop.backofficeservice.component;

import io.github.tonghoangvu.springsimpleshop.backofficeservice.model.enums.ErrorCode;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.autoconfigure.error.AbstractErrorController;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${spring.web.error.path:/error}")
@Hidden
@Slf4j
public class ApplicationErrorController extends AbstractErrorController {

  public ApplicationErrorController(ErrorAttributes errorAttributes) {
    super(errorAttributes);
  }

  @RequestMapping
  public ResponseEntity<Object> error(HttpServletRequest request) {
    logErrorDetails(request);
    HttpStatus status = getStatus(request);
    ProblemDetail body = createResponseBody(request, status);
    return new ResponseEntity<>(body, status);
  }

  private void logErrorDetails(HttpServletRequest request) {
    Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
    Throwable ex =
        request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) instanceof Throwable throwable
            ? throwable
            : null;
    if (logger.isErrorEnabled()) {
      logger.error("Unexpected error: {}", message, ex);
    }
  }

  private ProblemDetail createResponseBody(HttpServletRequest request, HttpStatus status) {
    ProblemDetail body = ProblemDetail.forStatus(status);
    if (request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) instanceof String requestUri) {
      body.setInstance(URI.create(requestUri)); // use the original request URI
    }
    body.setProperty("code", ErrorCode.resolve(status).getCode());
    body.setProperty("timestamp", OffsetDateTime.now());
    return body;
  }
}
