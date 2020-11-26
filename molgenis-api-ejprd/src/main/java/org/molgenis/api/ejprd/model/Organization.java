package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

@AutoValue
public abstract class Organization {

  public static Organization create(
      String id, String name, String description, String homepage, Location location) {
    return builder()
        .setId(id)
        .setName(name)
        .setDescription(description)
        .setHomepage(homepage)
        .setLocation(location)
        .build();
  }

  public static Organization.Builder builder() {
    return new AutoValue_Organization.Builder();
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

    public abstract Organization.Builder setId(String id);

    public abstract Organization.Builder setName(String name);

    public abstract Organization.Builder setDescription(String description);

    public abstract Organization.Builder setHomepage(String homepage);

    public abstract Organization.Builder setLocation(Location location);

    public abstract Organization build();
  }
}
