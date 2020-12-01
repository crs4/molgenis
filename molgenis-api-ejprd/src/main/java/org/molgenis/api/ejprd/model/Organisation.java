package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

@AutoValue
public abstract class Organisation {

  public static Organisation create(
      String id, String name, String description, String homepage, Location location) {
    return builder()
        .setId(id)
        .setName(name)
        .setDescription(description)
        .setHomepage(homepage)
        .setLocation(location)
        .build();
  }

  public static Organisation.Builder builder() {
    return new AutoValue_Organisation.Builder();
  }

  @SerializedName("id")
  public abstract String getId();

  @SerializedName("name")
  public abstract String getName();

  @Nullable
  @SerializedName("description")
  public abstract String getDescription();

  @Nullable
  @SerializedName("homepage")
  public abstract String getHomepage();

  @Nullable
  @SerializedName("location")
  public abstract Location getLocation();

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Organisation.Builder setId(String id);

    public abstract Organisation.Builder setName(String name);

    public abstract Organisation.Builder setDescription(String description);

    public abstract Organisation.Builder setHomepage(String homepage);

    public abstract Organisation.Builder setLocation(Location location);

    public abstract Organisation build();
  }
}
