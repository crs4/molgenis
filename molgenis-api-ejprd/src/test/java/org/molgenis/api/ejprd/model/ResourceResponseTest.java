package org.molgenis.api.ejprd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ResourceResponseTest {

  @Test
  void testBuildComplete() {
    Location location = Location.create("Location1", "IT", "Roma", "Lazio");
    Organisation organisation =
        Organisation.create(
            "ORG_1", "organisation 1 ", "This is the organisation 1", "http://org1.it", location);
    ResourceResponse resourceResponse =
        ResourceResponse.create(
            "biobank:1",
            "Biobank",
            "Biobank 1",
            "This is biobank 1",
            "https://biobank.url/",
            organisation);

    assertEquals(resourceResponse.getName(), "Biobank 1");
    assertEquals(resourceResponse.getHomepage(), "https://biobank.url/");
    assertEquals(resourceResponse.getId(), "biobank:1");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertEquals(resourceResponse.getDescription(), "This is biobank 1");
    assertEquals(resourceResponse.getPublisher(), organisation);
  }

  @Test
  void testBuildMissingOptionalFields() {
    ResourceResponse resourceResponse =
        ResourceResponse.create(
            "biobank:1", "Biobank", "Biobank 1", "Biobank", "https://biobank.url/", null);
    assertEquals(resourceResponse.getName(), "Biobank 1");
    assertEquals(resourceResponse.getHomepage(), "https://biobank.url/");
    assertEquals(resourceResponse.getId(), "biobank:1");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertEquals(resourceResponse.getDescription(), "Biobank");
    assertNull(resourceResponse.getPublisher());
  }

  @Test
  void testBuildMissingMandatoryFields() {
    assertThrows(
        NullPointerException.class,
        () -> {
          ResourceResponse.create(
              null, "Biobank", "Biobank 1", "Biobank", "https://biobank.url/", null);
        });
    assertThrows(
        NullPointerException.class,
        () -> {
          ResourceResponse.create(
              "biobank:1", null, "Biobank 1", "Biobank", "https://biobank.url/", null);
        });
    assertThrows(
        NullPointerException.class,
        () -> {
          ResourceResponse.create(
              "biobank:1", "Biobank", null, "Biobank", "https://biobank.url/", null);
        });
    assertThrows(
        NullPointerException.class,
        () -> {
          ResourceResponse.create(
              "biobank:1", "Biobank", "Biobank 1", null, "https://biobank.url/", null);
        });
  }

  @Test
  public void testFromJsonComplete() {
    String jsonString =
        "{"
            + "  \"name\": \"Biobank\","
            + "  \"homepage\": \"http://biobank.url/biobank:id\","
            + "  \"id\": \"biobank:id\","
            + "  \"type\": \"Biobank\","
            + "  \"description\": \"This is biobank 1\", "
            + "  \"publisher\": {"
            + "\"id\": \"Publisher_1\","
            + "\"name\": \"Publisher 1\","
            + "\"location\": {"
            + "\"id\": \"IT\","
            + "\"country\": \"Italy\""
            + "}"
            + "}"
            + "}";

    ResourceResponse resourceResponse = ResourceResponse.fromJson(jsonString);
    assertEquals(resourceResponse.getName(), "Biobank");
    assertEquals(resourceResponse.getHomepage(), "http://biobank.url/biobank:id");
    assertEquals(resourceResponse.getId(), "biobank:id");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertEquals(resourceResponse.getDescription(), "This is biobank 1");
  }

  @Test
  public void testFromJsonMissingOptionalFields() {
    String jsonString =
        "{"
            + "  \"name\": \"Biobank\","
            + "  \"description\": \" My Biobank\","
            + "  \"homepage\": \"http://biobank.url/biobank:id\","
            + "  \"id\": \"biobank:id\","
            + "  \"type\": \"Biobank\""
            + "}";

    ResourceResponse resourceResponse = ResourceResponse.fromJson(jsonString);
    assertEquals(resourceResponse.getName(), "Biobank");
    assertEquals(resourceResponse.getHomepage(), "http://biobank.url/biobank:id");
    assertEquals(resourceResponse.getId(), "biobank:id");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertNull(resourceResponse.getPublisher());
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

  @Test
  public void testFromJsonWithorganisationMissingOptionalFields() {
    String jsonString =
        "{"
            + "  \"name\": \"Biobank\","
            + "  \"homepage\": \"http://biobank.url/biobank:id\","
            + "  \"id\": \"biobank:id\","
            + "  \"type\": \"Biobank\","
            + "  \"description\": \"This is biobank 1\", "
            + "  \"publisher\": {"
            + "\"id\": \"Publisher_1\","
            + "\"name\": \"Publisher 1\""
            + "}"
            + "}";

    ResourceResponse resourceResponse = ResourceResponse.fromJson(jsonString);
    assertEquals(resourceResponse.getName(), "Biobank");
    assertEquals(resourceResponse.getHomepage(), "http://biobank.url/biobank:id");
    assertEquals(resourceResponse.getId(), "biobank:id");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertEquals(resourceResponse.getDescription(), "This is biobank 1");
    Organisation publisher = resourceResponse.getPublisher();
    assertEquals(publisher.getId(), "Publisher_1");
    assertEquals(publisher.getName(), "Publisher 1");
    assertEquals(publisher.getDescription(), null);
    assertEquals(publisher.getHomepage(), null);
    assertEquals(publisher.getLocation(), null);
  }

  @Test
  public void testFromJsonWithorganisationAndLocationMissingOptionalFields() {
    String jsonString =
        "{"
            + "  \"name\": \"Biobank\","
            + "  \"homepage\": \"http://biobank.url/biobank:id\","
            + "  \"id\": \"biobank:id\","
            + "  \"type\": \"Biobank\","
            + "  \"description\": \"This is biobank 1\", "
            + "  \"publisher\": {"
            + "\"id\": \"Publisher_1\","
            + "\"name\": \"Publisher 1\","
            + "\"location\": {"
            + "\"id\": \"IT\","
            + "\"country\": \"Italy\""
            + "}"
            + "}"
            + "}";

    ResourceResponse resourceResponse = ResourceResponse.fromJson(jsonString);
    assertEquals(resourceResponse.getName(), "Biobank");
    assertEquals(resourceResponse.getHomepage(), "http://biobank.url/biobank:id");
    assertEquals(resourceResponse.getId(), "biobank:id");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertEquals(resourceResponse.getDescription(), "This is biobank 1");
    Organisation publisher = resourceResponse.getPublisher();
    assertEquals(publisher.getId(), "Publisher_1");
    assertEquals(publisher.getName(), "Publisher 1");
    assertEquals(publisher.getDescription(), null);
    assertEquals(publisher.getHomepage(), null);
    Location location = publisher.getLocation();
    assertEquals(location.getId(), "IT");
    assertEquals(location.getCountry(), "Italy");
    assertEquals(location.getRegion(), null);
    assertEquals(location.getCity(), null);
  }

  public void testFromJsonWithorganisationAndLocationBothWithAllFields() {
    String jsonString =
        "{"
            + "  \"name\": \"Biobank\","
            + "  \"homepage\": \"http://biobank.url/biobank:id\","
            + "  \"id\": \"biobank:id\","
            + "  \"type\": \"Biobank\","
            + "  \"description\": \"This is biobank 1\", "
            + "  \"publisher\": {"
            + "\"id\": \"Publisher_1\","
            + "\"name\": \"Publisher 1\","
            + "\"description\": \"This is Publisher 1\","
            + "\"homepage\": \"http://www.publisher1.com\","
            + "\"location\": {"
            + "\"id\": \"IT\","
            + "\"country\": \"Italy\""
            + "\"city\": \"Roma\""
            + "\"region\": \"Lazio\""
            + "}"
            + "}"
            + "}";

    ResourceResponse resourceResponse = ResourceResponse.fromJson(jsonString);
    assertEquals(resourceResponse.getName(), "Biobank");
    assertEquals(resourceResponse.getHomepage(), "http://biobank.url/biobank:id");
    assertEquals(resourceResponse.getId(), "biobank:id");
    assertEquals(resourceResponse.getType(), "Biobank");
    assertEquals(resourceResponse.getDescription(), "This is biobank 1");
    Organisation publisher = resourceResponse.getPublisher();
    assertEquals(publisher.getId(), "Publisher_1");
    assertEquals(publisher.getName(), "Publisher 1");
    assertEquals(publisher.getDescription(), "This is Publisher 1");
    assertEquals(publisher.getHomepage(), "http://www.publisher1.com");
    Location location = publisher.getLocation();
    assertEquals(location.getId(), "IT");
    assertEquals(location.getCountry(), "Italy");
    assertEquals(location.getRegion(), "Lazio");
    assertEquals(location.getCity(), "Roma");
  }
}
