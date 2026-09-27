package io.github.tonghoangvu.springsimpleshop.backofficeservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  UNEXPECTED_ERROR("E001", "An unexpected error occurred"),
  CLIENT_ERROR("E002", "An error occurred on the client side"),
  SERVER_ERROR("E003", "An error occurred on the server side");

  private final String code;
  private final String description;

  public static ErrorCode resolve(HttpStatusCode status) {
    if (status.is4xxClientError()) {
      return CLIENT_ERROR;
    }
    if (status.is5xxServerError()) {
      return SERVER_ERROR;
    }
    return UNEXPECTED_ERROR;
  }
}
