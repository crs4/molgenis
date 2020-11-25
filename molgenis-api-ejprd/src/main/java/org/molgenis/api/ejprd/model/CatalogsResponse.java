package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import java.util.List;

@AutoValue
public abstract class CatalogsResponse {

  public static CatalogsResponse create(List<CatalogResponse> catalogs) {
    return builder().setCatalogs(catalogs).build();
  }

  public static Builder builder() {
    return new AutoValue_CatalogsResponse.Builder();
  }

  public abstract List<CatalogResponse> getCatalogs();

  // Abstract classes without fields should be converted to interfaces
  @SuppressWarnings({
    "java:S1610"
  }) // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setCatalogs(List<CatalogResponse> catalogs);

    public abstract CatalogsResponse build();
  }
}
