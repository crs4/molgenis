package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import org.molgenis.util.AutoGson;

@AutoValue
@AutoGson(autoValueClass = ResourceResponse.class)
public abstract class ResourceResponse {

  public abstract String getName();

  public abstract String getUrl();

  public abstract String getId();

  public abstract String getType();

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

  public static ResourceResponse fromJson(JsonObject jsonObject) {

    return ResourceResponse.create(
        jsonObject.get("name").getAsString(),
        jsonObject.get("url").getAsString(),
        jsonObject.get("id").getAsString(),
        jsonObject.get("type").getAsString(),
        jsonObject.get("description").getAsString(),
        null,
        null,
        null,
        null);
  }

  public static ResourceResponse fromJson(String jsonString) {
    Gson gson = new Gson();
    return gson.fromJson(jsonString, ResourceResponse.class);
    //    JsonObject jsonDataResponse = gson.fromJson(jsonString, JsonObject.class);
    //    return fromJson(jsonDataResponse);
  }

  public static ResourceResponse create(
      String name,
      String url,
      String id,
      String type,
      String description,
      String createDateTime,
      String updateDateTime,
      String version,
      String info) {
    return builder()
        .setName(name)
        .setUrl(url)
        .setId(id)
        .setType(type)
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

    public abstract Builder setType(String type);

    public abstract ResourceResponse build();
  }
}
