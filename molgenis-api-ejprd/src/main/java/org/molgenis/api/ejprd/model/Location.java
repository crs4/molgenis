package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

@AutoValue
public abstract class Location {

  public static Location create(String id, String country, String city, String region) {
    return builder().setId(id).setCountry(country).setCity(city).setRegion(region).build();
  }

  public static Location.Builder builder() {
    return new AutoValue_Location.Builder();
  }

  @SerializedName("id")
  public abstract String getId();

  @SerializedName("country")
  public abstract String getCountry();

  @Nullable
  @SerializedName("city")
  public abstract String getCity();

  @Nullable
  @SerializedName("region")
  public abstract String getRegion();

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Location.Builder setId(String id);

    public abstract Location.Builder setCountry(String country);

    public abstract Location.Builder setCity(String city);

    public abstract Location.Builder setRegion(String region);

    public abstract Location build();
  }
}
