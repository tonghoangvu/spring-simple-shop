package io.github.tonghoangvu.springsimpleshop.backofficeservice.model.exception;

import io.github.tonghoangvu.springsimpleshop.backofficeservice.model.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ServerException extends ApplicationException {

  public ServerException(String reason, Throwable cause) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause);
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.SERVER_ERROR;
  }
}
