package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ErrorResponse {

  public abstract Integer getCode();

  public abstract String getMessage();

  public static ErrorResponse create(Integer code, String message) {
    return builder().setCode(code).setMessage(message).build();
  }

  public static ErrorResponse.Builder builder() {
    return new AutoValue_ErrorResponse.Builder();
  }

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract ErrorResponse.Builder setCode(Integer code);

    public abstract ErrorResponse.Builder setMessage(String message);

    public abstract ErrorResponse build();
  }
}
