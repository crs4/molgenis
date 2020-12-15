package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import javax.annotation.Nullable;
import org.molgenis.api.model.response.PageResponse;

@AutoValue
public abstract class CatalogResponse {
  public static CatalogResponse create(
      String name, String url, List<ResourceResponse> resources, PageResponse page) {
    return builder().setName(name).setUrl(url).setResources(resources).setPage(page).build();
  }

  public static Builder builder() {
    return new AutoValue_CatalogResponse.Builder();
  }

  public abstract String getName();

  public abstract String getUrl();

  public abstract List<ResourceResponse> getResources();

  @Nullable
  @SerializedName("page")
  public abstract PageResponse getPage();

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setName(String name);

    public abstract Builder setUrl(String url);

    public abstract Builder setResources(List<ResourceResponse> resources);

    public abstract Builder setPage(PageResponse page);

    public abstract CatalogResponse build();
  }
}
