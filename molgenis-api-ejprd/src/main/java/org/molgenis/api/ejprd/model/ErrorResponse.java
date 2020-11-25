package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@AutoValue
public abstract class ErrorResponse {

  public static ErrorResponse create(Integer code, String message) {
    return builder().setCode(code).setMessage(message).build();
  }

  public static ErrorResponse fromJson(JsonObject jsonObject) {
    return ErrorResponse.create(
        jsonObject.get("code").getAsInt(), jsonObject.get("message").getAsString());
  }

  public static ErrorResponse fromJson(String jsonString) {
    Gson gson = new Gson();
    JsonObject jsonDataResponse = gson.fromJson(jsonString, JsonObject.class);
    return fromJson(jsonDataResponse);
  }

  public static ErrorResponse.Builder builder() {
    return new AutoValue_ErrorResponse.Builder();
  }

  public abstract Integer getCode();

  public abstract String getMessage();

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract ErrorResponse.Builder setCode(Integer code);

    public abstract ErrorResponse.Builder setMessage(String message);

    public abstract ErrorResponse build();
  }
}
