package org.molgenis.api.ejprd.exceptions;

import org.molgenis.api.ejprd.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class EJPRDResponseExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ExternalSourceNotFoundException.class)
  public final ResponseEntity<ErrorResponse> handleNotExistingExternalSource(
      ExternalSourceNotFoundException ex, WebRequest request) {
    ErrorResponse errorResponse = ErrorResponse.create(404, ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }
}
