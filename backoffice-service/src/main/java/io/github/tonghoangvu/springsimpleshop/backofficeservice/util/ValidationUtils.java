package io.github.tonghoangvu.springsimpleshop.backofficeservice.util;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

public class ValidationUtils {

  public static List<String> extractErrorMessages(MethodArgumentNotValidException ex) {
    return Stream.concat(ex.getFieldErrors().stream(), ex.getGlobalErrors().stream())
        .map(error -> buildErrorMessage(ex.getParameter(), error))
        .toList();
  }

  public static List<String> extractErrorMessages(HandlerMethodValidationException ex) {
    Stream<String> parameterErrorMessages =
        ex.getParameterValidationResults().stream()
            .flatMap(
                result ->
                    result.getResolvableErrors().stream()
                        .map(error -> buildErrorMessage(result.getMethodParameter(), error)));
    Stream<String> crossParameterErrorMessages =
        ex.getCrossParameterValidationResults().stream().map(ValidationUtils::buildErrorMessage);
    return Stream.concat(parameterErrorMessages, crossParameterErrorMessages).toList();
  }

  private static String buildErrorMessage(
      MethodParameter parameter, MessageSourceResolvable error) {
    if (error instanceof FieldError fieldError) {
      return String.format(
          "%s.%s: %s",
          parameter.getParameterName(), fieldError.getField(), fieldError.getDefaultMessage());
    }
    return String.format("%s: %s", parameter.getParameterName(), error.getDefaultMessage());
  }

  private static String buildErrorMessage(MessageSourceResolvable error) {
    if (error instanceof FieldError fieldError) {
      return String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
    }
    return error.getDefaultMessage();
  }
}
