package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.molgenis.api.model.response.PageResponse;

@AutoValue
public abstract class DataResponse {

  public abstract String getApiVersion();

  public abstract List<ResourceResponse> getResourceResponses();

  @Nullable
  public abstract PageResponse getPage();

  @Nullable
  public abstract ErrorResponse getError();

  public static DataResponse fromJson(String jsonString) {
    Gson gson = new Gson();
    JsonObject jsonDataResponse = gson.fromJson(jsonString, JsonElement.class).getAsJsonObject();
    String version = jsonDataResponse.getAsJsonPrimitive("apiVersion").getAsString();
    JsonArray jsonResources = jsonDataResponse.getAsJsonArray("resourceResponses");

    List<ResourceResponse> resources = new ArrayList<>();
    for (JsonElement resource : jsonResources) {
      ResourceResponse resourceResponse = ResourceResponse.fromJson(resource.getAsJsonObject());
      resources.add(resourceResponse);
    }
    return DataResponse.create(version, resources, null, null);
  }

  public static DataResponse create(
      String apiVersion,
      List<ResourceResponse> resourcesResponses,
      PageResponse page,
      ErrorResponse error) {
    return builder()
        .setApiVersion(apiVersion)
        .setResourceResponses(resourcesResponses)
        .setPage(page)
        .setError(error)
        .build();
  }

  public static DataResponse.Builder builder() {
    return new AutoValue_DataResponse.Builder();
  }

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract DataResponse.Builder setApiVersion(String apiVersion);

    public abstract DataResponse.Builder setResourceResponses(
        List<ResourceResponse> resourcesResponses);

    public abstract DataResponse.Builder setError(ErrorResponse error);

    public abstract DataResponse.Builder setPage(PageResponse page);

    public abstract DataResponse build();
  }
}
