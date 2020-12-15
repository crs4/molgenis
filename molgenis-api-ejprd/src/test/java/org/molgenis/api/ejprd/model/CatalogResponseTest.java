package org.molgenis.api.ejprd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.molgenis.api.model.response.PageResponse;

public class CatalogResponseTest {

  @Test
  public void testCatalogResponseComplete() {
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
    List<ResourceResponse> resourceResponses = new ArrayList<>();
    resourceResponses.add(resourceResponse);
    PageResponse page = PageResponse.create(5, 10, 1);

    CatalogResponse catalogResponse =
        CatalogResponse.create("Catalog 1", "http://www.catalog1.it", resourceResponses, page);

    assertEquals(catalogResponse.getName(), "Catalog 1");
    assertEquals(catalogResponse.getUrl(), "http://www.catalog1.it");
    assertEquals(catalogResponse.getResources(), resourceResponses);
    assertEquals(catalogResponse.getResources().get(0).getId(), "biobank:1");
    assertEquals(catalogResponse.getPage().getSize(), 5);
    assertEquals(catalogResponse.getPage().getNumber(), 1);
    assertEquals(catalogResponse.getPage().getTotalElements(), 10);
  }

  @Test
  public void testCatalogResponseMissingMandatoryField() {
    Location location = Location.create("Location1", "IT", "Roma", "Lazio");
    Organisation organisation =
        Organisation.create(
            "ORG_1", "Organisation 1 ", "This is the organisation 1", "http://org1.it", location);
    ResourceResponse resourceResponse =
        ResourceResponse.create(
            "biobank:1",
            "Biobank",
            "Biobank 1",
            "This is biobank 1",
            "https://biobank.url/",
            organisation);
    List<ResourceResponse> resourceResponses = new ArrayList<>();
    resourceResponses.add(resourceResponse);

    assertThrows(
        NullPointerException.class,
        () -> {
          CatalogResponse.create(null, "http://www.catalog1.it", resourceResponses, null);
        });

    assertThrows(
        NullPointerException.class,
        () -> {
          CatalogResponse.create("Catalog 1", null, resourceResponses, null);
        });
  }
}
