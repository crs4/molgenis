package org.molgenis.api.ejprd.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.molgenis.api.model.response.PageResponse;
import org.molgenis.util.AutoGson;

@AutoValue
@AutoGson(autoValueClass = AutoValue_DataResponse.class)
public abstract class DataResponse {

  @SerializedName("apiVersion")
  public abstract String getApiVersion();

  @SerializedName("resourceResponses")
  public abstract List<ResourceResponse> getResourceResponses();

  @Nullable
  @SerializedName("page")
  public abstract PageResponse getPage();

  @Nullable
  @SerializedName("error")
  public abstract ErrorResponse getError();

  public static DataResponse fromJson(JsonObject jsonDataResponse) {
    JsonPrimitive jsonVersion = jsonDataResponse.getAsJsonPrimitive("apiVersion");
    String version = jsonVersion != null ? jsonVersion.getAsString() : null;

    JsonArray jsonResources = jsonDataResponse.getAsJsonArray("resourceResponses");
    List<ResourceResponse> resources = null;
    if (jsonResources != null) {
      resources = new ArrayList<>();
      for (JsonElement resource : jsonResources) {
        ResourceResponse resourceResponse = ResourceResponse.fromJson(resource.getAsJsonObject());
        resources.add(resourceResponse);
      }
    }

    PageResponse pageResponse = null;
    JsonObject jsonPage = jsonDataResponse.getAsJsonObject("page");
    if (jsonPage != null) {
          pageResponse = PageResponse.create(
              jsonPage.getAsJsonPrimitive("size").getAsInt(),
              jsonPage.getAsJsonPrimitive("totalElements").getAsInt(),
              jsonPage.getAsJsonPrimitive("number").getAsInt());
    }

    ErrorResponse errorResponse = null;
    JsonObject jsonError = jsonDataResponse.getAsJsonObject("error");
    if (jsonError != null) {
      errorResponse = ErrorResponse.fromJson(jsonError);
    }
    return DataResponse.create(version, resources, pageResponse, errorResponse);
  }

  public static DataResponse fromJson(String jsonString) {
    Gson gson = new Gson();
    JsonObject jsonDataResponse = gson.fromJson(jsonString, JsonObject.class);
    return fromJson(jsonDataResponse);
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
