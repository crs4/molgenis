package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

@AutoValue
public abstract class ResourceResponse {

  public static ResourceResponse fromJson(JsonObject jsonObject) {

    Organization organization = null;
    Location location = null;

    if (jsonObject.get("publisher") != null) {
      JsonObject publisher = jsonObject.get("publisher").getAsJsonObject();
      String publisherName = publisher.get("name").getAsString();
      String publisherId = publisher.get("id").getAsString();

      if (publisher.get("location") != null) {
        JsonObject publisherLocation = publisher.get("location").getAsJsonObject();
        String publisherLocationId = publisherLocation.get("id").getAsString();
        String publisherLocationCountry = publisherLocation.get("country").getAsString();
        location =
            Location.create(
                publisherLocationId,
                publisherLocationCountry,
                publisherLocation.get("city") != null
                    ? publisherLocation.get("city").getAsString()
                    : null,
                publisherLocation.get("region") != null
                    ? publisherLocation.get("region").getAsString()
                    : null);
      }
      organization =
          Organization.create(
              publisherId,
              publisherName,
              publisher.get("description") != null
                  ? publisher.get("description").getAsString()
                  : null,
              publisher.get("homepage") != null ? publisher.get("homepage").getAsString() : null,
              location);
    }

    return ResourceResponse.create(
        jsonObject.get("id") != null ? jsonObject.get("id").getAsString() : null,
        jsonObject.get("type") != null ? jsonObject.get("type").getAsString() : null,
        jsonObject.get("name") != null ? jsonObject.get("name").getAsString() : null,
        jsonObject.get("description") != null ? jsonObject.get("description").getAsString() : null,
        jsonObject.get("homepage") != null
            ? jsonObject.get("homepage").getAsString()
            : null, // ,null
        organization);
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
