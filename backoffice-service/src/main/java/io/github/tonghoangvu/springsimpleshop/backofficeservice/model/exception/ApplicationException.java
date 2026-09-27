package io.github.tonghoangvu.springsimpleshop.backofficeservice.model.exception;

import io.github.tonghoangvu.springsimpleshop.backofficeservice.model.enums.ErrorCode;
import java.util.Locale;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.server.ResponseStatusException;

public abstract class ApplicationException extends ResponseStatusException {

  public ApplicationException(HttpStatusCode status, String reason, Throwable cause) {
    super(status, reason, cause);
  }

  @Override
  public @NonNull ProblemDetail updateAndGetBody(
      MessageSource messageSource, @NonNull Locale locale) {
    super.updateAndGetBody(messageSource, locale);
    if (getErrorCode() != null) {
      getBody().setProperty("code", getErrorCode().getCode());
    }
    return getBody();
  }

  public abstract ErrorCode getErrorCode();
}
