package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import java.util.List;

@AutoValue
public abstract class CatalogResponse {
  public static CatalogResponse create(String name, String url, List<ResourceResponse> resources) {
    return builder().setName(name).setUrl(url).setResources(resources).build();
  }

  public static Builder builder() {
    return new AutoValue_CatalogResponse.Builder();
  }

  public abstract String getName();

  public abstract String getUrl();

  public abstract List<ResourceResponse> getResources();

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setName(String name);

    public abstract Builder setUrl(String url);

    public abstract Builder setResources(List<ResourceResponse> resources);

    public abstract CatalogResponse build();
  }
}
