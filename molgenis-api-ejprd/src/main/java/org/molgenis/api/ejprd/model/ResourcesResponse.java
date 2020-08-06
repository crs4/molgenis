package org.molgenis.api.ejprd.model;

import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class ResourcesResponse {

  private final List<ResourceResponse> items;

  private ResourcesResponse(@Nullable List<ResourceResponse> items) {
    this.items = items;
  }

  @Nullable
  @CheckForNull
  public List<ResourceResponse> getItems() {
    return items;
  }

  public static ResourcesResponse create(List<ResourceResponse> newItems) {
    return builder().setItems(newItems).build();
  }

  public static Builder builder() {
    return new ResourcesResponse.Builder();
  }

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  public static class Builder {

    private List<ResourceResponse> items;

    public Builder setItems(List<ResourceResponse> newItems) {
      this.items = newItems;
      return this;
    }

    public ResourcesResponse build() {
      return new ResourcesResponse(this.items);
    }
  }
}
