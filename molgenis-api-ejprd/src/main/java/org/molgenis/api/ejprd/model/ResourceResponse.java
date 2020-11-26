package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

@AutoValue
public abstract class ResourceResponse {

  public static ResourceResponse fromJson(JsonObject jsonObject) {

    return ResourceResponse.create(
        jsonObject.get("id") != null ? jsonObject.get("id").getAsString() : null,
        jsonObject.get("name") != null ? jsonObject.get("name").getAsString() : null,
        jsonObject.get("type") != null ? jsonObject.get("type").getAsString() : null,
        jsonObject.get("description") != null ? jsonObject.get("description").getAsString() : null,
        jsonObject.get("homepage") != null ? jsonObject.get("homepage").getAsString() : null,
        // TODO: import publisher from external when not null
        null);
  }

  public static ResourceResponse fromJson(String jsonString) {
    Gson gson = new Gson();
    JsonObject jsonDataResponse = gson.fromJson(jsonString, JsonObject.class);
    return fromJson(jsonDataResponse);
  }

  public static ResourceResponse create(
      String id,
      String type,
      String name,
      String description,
      String homepage,
      Organization publisher) {
    return builder()
        .setId(id)
        .setType(type)
        .setName(name)
        .setDescription(description)
        .setHomepage(homepage)
        .setPublisher(publisher)
        .build();
  }

  public static Builder builder() {
    return new AutoValue_ResourceResponse.Builder();
  }

  public abstract String getId();

  public abstract String getName();

  public abstract String getType();

  public abstract String getDescription();

  public abstract String getHomepage();

  @Nullable
  public abstract Organization getPublisher();

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract ResourceResponse.Builder setId(String id);

    public abstract ResourceResponse.Builder setName(String name);

    public abstract Builder setType(String type);

    public abstract ResourceResponse.Builder setDescription(String description);

    public abstract ResourceResponse.Builder setHomepage(String homepage);

    public abstract ResourceResponse.Builder setPublisher(Organization publisher);

    public abstract ResourceResponse build();
  }
}
