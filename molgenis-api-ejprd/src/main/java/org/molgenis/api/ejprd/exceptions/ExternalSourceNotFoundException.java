package org.molgenis.api.ejprd.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExternalSourceNotFoundException extends RuntimeException {
  public ExternalSourceNotFoundException(HttpStatus notFound, String errorMessage) {
    super(errorMessage);
  }
}
