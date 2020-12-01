package org.molgenis.api.ejprd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.molgenis.api.model.response.PageResponse;

public class DataResponseTest {

  @Test
  void testBuildComplete() {
    String apiVersion = "v0.2";
    ResourceResponse resourceResponse =
        ResourceResponse.create(
            "Biobank 1", "biobank:1", "https://biobank.url/", "Biobank", "This is biobank 1", null);
    List<ResourceResponse> resourceResponses = Collections.singletonList(resourceResponse);
    PageResponse pageResponse = PageResponse.create(2, 10, 1);
    ErrorResponse errorResponse = ErrorResponse.create(1, "Error");
    DataResponse dataResponse =
        DataResponse.create(apiVersion, resourceResponses, pageResponse, errorResponse);
    assertEquals(dataResponse.getApiVersion(), apiVersion);
    assertEquals(dataResponse.getResourceResponses(), Collections.singletonList(resourceResponse));
    assertEquals(dataResponse.getPage(), pageResponse);
    assertEquals(dataResponse.getError(), errorResponse);
  }

  @Test
  void testBuildMissingOptionalFields() {
    String apiVersion = "v0.2";
    ResourceResponse resourceResponse =
        ResourceResponse.create(
            "Biobank 1", "biobank:1", "https://biobank.url/", "Biobank", "This is biobank 1", null);
    List<ResourceResponse> resourceResponses = Collections.singletonList(resourceResponse);
    DataResponse dataResponse = DataResponse.create(apiVersion, resourceResponses, null, null);
    assertEquals(dataResponse.getApiVersion(), apiVersion);
    assertEquals(dataResponse.getResourceResponses(), Collections.singletonList(resourceResponse));
    assertNull(dataResponse.getPage());
    assertNull(dataResponse.getError());
  }

  @Test
  void testBuildMissingMandatoryFields() {
    String apiVersion = "v0.2";
    ResourceResponse resourceResponse =
        ResourceResponse.create(
            "Biobank 1", "biobank:1", "https://biobank.url/", "Biobank", "This is biobank 1", null);
    List<ResourceResponse> resourceResponses = Collections.singletonList(resourceResponse);
    assertThrows(
        NullPointerException.class,
        () -> {
          DataResponse.create(apiVersion, null, null, null);
        });

    assertThrows(
        NullPointerException.class,
        () -> {
          DataResponse.create(null, resourceResponses, null, null);
        });
  }

  @Test
  public void testFromJsonComplete() {
    String jsonString =
        "{\n"
            + "    \"apiVersion\": \"v0.2\","
            + "    \"resourceResponses\": ["
            + "        {"
            + "            \"name\": \"Biobank\","
            + "            \"homepage\": \"http://biobank.url/biobank_id\","
            + "            \"id\": \"biobank_id\","
            + "            \"type\": \"Biobank\","
            + "            \"description\": \"Biobank description\""
            + "        }"
            + "    ],"
            + "    \"page\": {"
            + "        \"size\": 1,"
            + "        \"totalElements\": 1,"
            + "        \"totalPages\": 1,"
            + "        \"number\": 0"
            + "    },"
            + "    \"error\": {"
            + "        \"code\": 1,"
            + "        \"message\": \"Something wrong\""
            + "    }"
            + "}";
    DataResponse dataResponse = DataResponse.fromJson(jsonString);
    assertEquals(dataResponse.getApiVersion(), "v0.2");
    assertEquals(dataResponse.getResourceResponses().size(), 1);
    assertEquals(dataResponse.getResourceResponses().get(0).getName(), "Biobank");
    assertEquals(
        dataResponse.getResourceResponses().get(0).getHomepage(), "http://biobank.url/biobank_id");
    assertEquals(dataResponse.getResourceResponses().get(0).getId(), "biobank_id");
    assertEquals(dataResponse.getResourceResponses().get(0).getType(), "Biobank");
    assertEquals(
        dataResponse.getResourceResponses().get(0).getDescription(), "Biobank description");
    assertNotEquals(dataResponse.getPage(), null);
    assertEquals(dataResponse.getPage().getSize(), 1);
    assertEquals(dataResponse.getPage().getTotalElements(), 1);
    assertEquals(dataResponse.getPage().getTotalPages(), 1);
    assertEquals(dataResponse.getPage().getNumber(), 0);
    assertNotEquals(dataResponse.getError(), null);
    assertEquals(dataResponse.getError().getCode(), 1);
    assertEquals(dataResponse.getError().getMessage(), "Something wrong");
  }

  @Test
  public void testFromJsonMissingOptionalFields() {
    String jsonString =
        "{"
            + "\"apiVersion\": \"v0.2\","
            + "\"resourceResponses\": ["
            + "{"
            + "   \"name\": \"Biobank\","
            + "   \"homepage\": \"http://biobank.url/biobank_id\","
            + "   \"id\": \"biobank_id\","
            + "   \"type\": \"Biobank\","
            + "   \"description\": \"Biobank description\""
            + "}]"
            + "}";
    DataResponse dataResponse = DataResponse.fromJson(jsonString);
    assertEquals(dataResponse.getApiVersion(), "v0.2");
    assertEquals(dataResponse.getResourceResponses().size(), 1);
    assertEquals(dataResponse.getResourceResponses().get(0).getName(), "Biobank");
    assertEquals(
        dataResponse.getResourceResponses().get(0).getHomepage(), "http://biobank.url/biobank_id");
    assertEquals(dataResponse.getResourceResponses().get(0).getId(), "biobank_id");
    assertEquals(dataResponse.getResourceResponses().get(0).getType(), "Biobank");
    assertEquals(
        dataResponse.getResourceResponses().get(0).getDescription(), "Biobank description");

    assertNull(dataResponse.getPage());
    assertNull(dataResponse.getError());
  }

  @Test
  public void testFromJsonMissingMandatoryFields() {
    String jsonStringWithVersion = "{\"apiVersion\": \"v0.2\"}";
    assertThrows(
        NullPointerException.class,
        () -> {
          DataResponse.fromJson(jsonStringWithVersion);
        });
    String jsonStringWithResources =
        "{"
            + "\"resourceResponses\": ["
            + "{"
            + "   \"name\": \"Biobank\","
            + "   \"homepage\": \"http://biobank.url/biobank_id\","
            + "   \"id\": \"biobank_id\","
            + "   \"type\": \"Biobank\","
            + "   \"description\": \"Biobank description\""
            + "}]"
            + "}";
    assertThrows(
        NullPointerException.class,
        () -> {
          DataResponse.fromJson(jsonStringWithResources);
        });
  }
}
