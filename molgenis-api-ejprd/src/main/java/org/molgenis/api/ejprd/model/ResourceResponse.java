package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

@AutoValue
public abstract class ResourceResponse {

  public abstract String getType();

  public abstract String getName();

  public abstract String getUrl();

  public abstract String getUuid();

  @Nullable
  public abstract String getDescription();

  public static ResourceResponse create(
      String type, String name, String url, String uuid, String description) {
    return builder()
        .setType(type)
        .setName(name)
        .setUrl(url)
        .setUuid(uuid)
        .setDescription(description)
        .build();
  }

  public static Builder builder() {
    return new AutoValue_ResourceResponse.Builder();
  }

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract ResourceResponse.Builder setType(String type);

    public abstract ResourceResponse.Builder setName(String name);

    public abstract ResourceResponse.Builder setUrl(String url);

    public abstract ResourceResponse.Builder setUuid(String uuid);

    public abstract ResourceResponse.Builder setDescription(String description);

    public abstract ResourceResponse build();
  }
}
