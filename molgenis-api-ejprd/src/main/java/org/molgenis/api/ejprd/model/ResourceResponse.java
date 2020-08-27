package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

@AutoValue
public abstract class ResourceResponse {

  public abstract String getName();

  public abstract String getUrl();

  public abstract String getId();

  @Nullable
  public abstract String getDescription();

  @Nullable
  public abstract String getCreateDateTime();

  @Nullable
  public abstract String getUpdateDateTime();

  @Nullable
  public abstract String getVersion();

  @Nullable
  public abstract String getInfo();

  public static ResourceResponse create(
      String name,
      String url,
      String id,
      String description,
      String createDateTime,
      String updateDateTime,
      String version,
      String info) {
    return builder()
        .setName(name)
        .setUrl(url)
        .setId(id)
        .setDescription(description)
        .setCreateDateTime(createDateTime)
        .setUpdateDateTime(updateDateTime)
        .setVersion(version)
        .setInfo(info)
        .build();
  }

  public static Builder builder() {
    return new AutoValue_ResourceResponse.Builder();
  }

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract ResourceResponse.Builder setName(String name);

    public abstract ResourceResponse.Builder setUrl(String url);

    public abstract ResourceResponse.Builder setId(String id);

    public abstract ResourceResponse.Builder setDescription(String description);

    public abstract ResourceResponse.Builder setCreateDateTime(String createDateTime);

    public abstract ResourceResponse.Builder setUpdateDateTime(String updateDateTime);

    public abstract ResourceResponse.Builder setVersion(String version);

    public abstract ResourceResponse.Builder setInfo(String info);

    public abstract ResourceResponse build();
  }
}
