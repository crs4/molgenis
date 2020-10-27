package org.molgenis.api.ejprd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ResourceResponseTest {

  @Test
  void testBuildComplete() {
    ResourceResponse resourceResponse =
        ResourceResponse.create(
            "Biobank 1",
            "https://biobank.url/",
            "biobank:1",
            "Biobank",
            "This is biobank 1",
            "2020-01-01",
            "2020-01-01",
            "v1",
            "Some info about this biobank");
    assertEquals(resourceResponse.getName(), "Biobank 1");
    assertEquals(resourceResponse.getUrl(), "https://biobank.url/");
    assertEquals(resourceResponse.getId(), "biobank:1");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertEquals(resourceResponse.getDescription(), "This is biobank 1");
    assertEquals(resourceResponse.getCreateDateTime(), "2020-01-01");
    assertEquals(resourceResponse.getUpdateDateTime(), "2020-01-01");
    assertEquals(resourceResponse.getVersion(), "v1");
    assertEquals(resourceResponse.getInfo(), "Some info about this biobank");
  }

  @Test
  void testBuildMissingOptionalFields() {
    ResourceResponse resourceResponse =
        ResourceResponse.create(
            "Biobank 1",
            "https://biobank.url/",
            "biobank:1",
            "Biobank",
            null,
            null,
            null,
            null,
            null);
    assertEquals(resourceResponse.getName(), "Biobank 1");
    assertEquals(resourceResponse.getUrl(), "https://biobank.url/");
    assertEquals(resourceResponse.getId(), "biobank:1");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertNull(resourceResponse.getDescription());
    assertNull(resourceResponse.getCreateDateTime());
    assertNull(resourceResponse.getUpdateDateTime());
    assertNull(resourceResponse.getVersion());
    assertNull(resourceResponse.getInfo());
  }

  @Test
  void testBuildMissingMandatoryFields() {
    assertThrows(
        NullPointerException.class,
        () -> {
          ResourceResponse.create(
              null,
              "https://biobank.url/",
              "biobank:1",
              "Biobank",
              "This is biobank 1",
              null,
              null,
              null,
              null);
        });
    assertThrows(
        NullPointerException.class,
        () -> {
          ResourceResponse.create(
              "Biobank 1",
              null,
              "biobank:1",
              "Biobank",
              "This is biobank 1",
              null,
              null,
              null,
              null);
        });
    assertThrows(
        NullPointerException.class,
        () -> {
          ResourceResponse.create(
              "Biobank 1",
              "https://biobank.url/",
              null,
              "Biobank",
              "This is biobank 1",
              null,
              null,
              null,
              null);
        });
    assertThrows(
        NullPointerException.class,
        () -> {
          ResourceResponse.create(
              "Biobank 1",
              "https://biobank.url/",
              "biobank:1",
              null,
              "This is biobank 1",
              null,
              null,
              null,
              null);
        });
  }

  @Test
  public void testFromJsonComplete() {
    String jsonString =
        "{"
            + "  \"name\": \"Biobank\","
            + "  \"url\": \"http://biobank.url/biobank:id\","
            + "  \"id\": \"biobank:id\","
            + "  \"type\": \"Biobank\","
            + "  \"description\": \"This is biobank 1\","
            + "  \"createDateTime\": \"2020-01-01\","
            + "  \"updateDateTime\": \"2020-01-01\","
            + "  \"version\": \"v1\","
            + "  \"info\": \"Some info about this biobank\""
            + "}";

    ResourceResponse resourceResponse = ResourceResponse.fromJson(jsonString);
    assertEquals(resourceResponse.getName(), "Biobank");
    assertEquals(resourceResponse.getUrl(), "http://biobank.url/biobank:id");
    assertEquals(resourceResponse.getId(), "biobank:id");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertEquals(resourceResponse.getDescription(), "This is biobank 1");
    assertEquals(resourceResponse.getCreateDateTime(), "2020-01-01");
    assertEquals(resourceResponse.getUpdateDateTime(), "2020-01-01");
    assertEquals(resourceResponse.getVersion(), "v1");
    assertEquals(resourceResponse.getInfo(), "Some info about this biobank");
  }

  @Test
  public void testFromJsonMissingOptionalFields() {
    String jsonString =
        "{"
            + "  \"name\": \"Biobank\","
            + "  \"url\": \"http://biobank.url/biobank:id\","
            + "  \"id\": \"biobank:id\","
            + "  \"type\": \"Biobank\""
            + "}";

    ResourceResponse resourceResponse = ResourceResponse.fromJson(jsonString);
    assertEquals(resourceResponse.getName(), "Biobank");
    assertEquals(resourceResponse.getUrl(), "http://biobank.url/biobank:id");
    assertEquals(resourceResponse.getId(), "biobank:id");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertNull(resourceResponse.getDescription());
    assertNull(resourceResponse.getCreateDateTime());
    assertNull(resourceResponse.getUpdateDateTime());
    assertNull(resourceResponse.getVersion());
    assertNull(resourceResponse.getInfo());
  }

  @Test
  public void testFromJsonMissingMandatoryFields() {
    String jsonStringWithoutNameResources =
        "{"
            + "   \"name\": \"Biobank\","
            + "   \"url\": \"http://biobank.url/biobank:id\","
            + "   \"id\": \"biobank:id\","
            + "   \"type\": \"Biobank\""
            + "}";
    assertThrows(
        NullPointerException.class,
        () -> {
          DataResponse.fromJson(jsonStringWithoutNameResources);
        });
    String jsonStringWithoutUrl =
        "{"
            + "   \"name\": \"Biobank\","
            + "   \"url\": \"http://biobank.url/biobank:id\","
            + "   \"id\": \"biobank:id\","
            + "   \"type\": \"Biobank\""
            + "}";
    assertThrows(
        NullPointerException.class,
        () -> {
          DataResponse.fromJson(jsonStringWithoutUrl);
        });
    String jsonStringWithoutId =
        "{"
            + "   \"name\": \"Biobank\","
            + "   \"url\": \"http://biobank.url/biobank:id\","
            + "   \"id\": \"biobank:id\","
            + "   \"type\": \"Biobank\""
            + "}";
    assertThrows(
        NullPointerException.class,
        () -> {
          DataResponse.fromJson(jsonStringWithoutId);
        });
    String jsonStringWithoutType =
        "{"
            + "   \"name\": \"Biobank\","
            + "   \"url\": \"http://biobank.url/biobank:id\","
            + "   \"id\": \"biobank:id\","
            + "   \"type\": \"Biobank\""
            + "}";
    assertThrows(
        NullPointerException.class,
        () -> {
          DataResponse.fromJson(jsonStringWithoutType);
        });
  }
}
