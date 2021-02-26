package org.molgenis.api.ejprd.exceptions;

import javax.annotation.Nullable;
import org.molgenis.api.ejprd.model.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EJPRDResponseExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ExternalSourceNotFoundException.class)
  public final ResponseEntity<ErrorResponse> handleNotExistingExternalSource(
      ExternalSourceNotFoundException ex, WebRequest request) {
    ErrorResponse errorResponse = ErrorResponse.create(404, ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ExternalSourceErrorException.class)
  public final ResponseEntity<ErrorResponse> handleNotExistingExternalSource(
      ExternalSourceErrorException ex, WebRequest request) {
    ErrorResponse errorResponse = ErrorResponse.create(500, ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex,
      @Nullable Object body,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    if (HttpStatus.BAD_REQUEST.equals(status)) {
      ErrorResponse errorResponse = ErrorResponse.create(400, "Invalid request parameter(s)");
      return new ResponseEntity<>(errorResponse, headers, HttpStatus.BAD_REQUEST);
    }
    return super.handleExceptionInternal(ex, body, headers, status, request);
  }
}
