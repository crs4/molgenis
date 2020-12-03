package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import javax.annotation.Nullable;
import org.molgenis.api.ejprd.model.CatalogResponse.Builder;

@AutoValue
public abstract class CatalogInfoResponse {

  public static CatalogInfoResponse create(
      String id,
      ArrayList<String> type,
      String name,
      String description,
      String homepage,
      Organisation organisation,
      String apiVersion,
      ArrayList<String> sampleRequests) {
    return builder()
        .setId(id)
        .setType(type)
        .setName(name)
        .setDescription(description)
        .setHomepage(homepage)
        .setOrganisation(organisation)
        .setApiVersion(apiVersion)
        .setSampleRequests(sampleRequests)
        .build();
  }

  public static Builder builder() {
    return new AutoValue_CatalogInfoResponse.Builder();
  }

  @SerializedName("id")
  public abstract String getId();

  @SerializedName("type")
  public abstract ArrayList<String> getType();

  @Nullable
  @SerializedName("name")
  public abstract String getName();

  @Nullable
  @SerializedName("description")
  public abstract String getDescription();

  @Nullable
  @SerializedName("homepage")
  public abstract String getHomepage();

  @Nullable
  @SerializedName("organisation")
  public abstract Organisation getOrganisation();

  @Nullable
  @SerializedName("apiVersion")
  public abstract String getApiVersion();

  @Nullable
  @SerializedName("sampleRequests")
  public abstract ArrayList<String> getSampleRequests();

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setId(String id);

    public abstract Builder setType(ArrayList<String> type);

    public abstract Builder setName(String name);

    public abstract Builder setDescription(String description);

    public abstract Builder setHomepage(String homepage);

    public abstract Builder setOrganisation(Organisation organisation);

    public abstract Builder setApiVersion(String apiVersion);

    public abstract Builder setSampleRequests(ArrayList<String> sampleRequests);

    public abstract CatalogInfoResponse build();
  }
}
