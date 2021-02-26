package org.molgenis.api.ejprd.exceptions;

public class ExternalSourceErrorException extends RuntimeException {

  private static final String errorMessage = "Error querying external resource";

  public ExternalSourceErrorException() {
    super(errorMessage);
  }
}
