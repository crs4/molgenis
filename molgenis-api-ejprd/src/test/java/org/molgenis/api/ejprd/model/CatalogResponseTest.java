package org.molgenis.api.ejprd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

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
    List<ResourceResponse> resourceResponses = new ArrayList();
    resourceResponses.add(resourceResponse);

    CatalogResponse catalogResponse =
        CatalogResponse.create("Catalog 1", "http://www.catalog1.it", resourceResponses);

    assertEquals(catalogResponse.getName(), "Catalog 1");
    assertEquals(catalogResponse.getUrl(), "http://www.catalog1.it");
    assertEquals(catalogResponse.getResources(), resourceResponses);
    assertEquals(catalogResponse.getResources().get(0).getId(), "biobank:1");
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
    List<ResourceResponse> resourceResponses = new ArrayList();
    resourceResponses.add(resourceResponse);

    assertThrows(
        NullPointerException.class,
        () -> {
          CatalogResponse.create(null, "http://www.catalog1.it", resourceResponses);
        });

    assertThrows(
        NullPointerException.class,
        () -> {
          CatalogResponse.create("Catalog 1", null, resourceResponses);
        });
  }

  @Test
  public void testCatalogsResponseComplete() {
    Location location1 = Location.create("Location1", "IT", "Roma", "Lazio");
    Organisation organisation1 =
        Organisation.create(
            "ORG_1", "organisation 1 ", "This is the organisation 1", "http://org1.it", location1);
    ResourceResponse resourceResponse1 =
        ResourceResponse.create(
            "biobank:1",
            "Biobank",
            "Biobank 1",
            "This is biobank 1",
            "https://biobank.url/",
            organisation1);
    List<ResourceResponse> resourceResponses = new ArrayList();
    resourceResponses.add(resourceResponse1);

    CatalogResponse catalogResponse1 =
        CatalogResponse.create("Catalog 1", "http://www.catalog1.it", resourceResponses);

    Location location2 = Location.create("Location2", "IT", "Cagliari", "Sardegna");
    Organisation organisation2 =
        Organisation.create(
            "ORG_2", "organisation 2 ", "This is the organisation 2", "http://org2.it", location2);
    ResourceResponse resourceResponse2 =
        ResourceResponse.create(
            "biobank:1",
            "Biobank",
            "Biobank 1",
            "This is biobank 1",
            "https://biobank.url/",
            organisation2);
    List<ResourceResponse> resourceResponses2 = new ArrayList();
    resourceResponses2.add(resourceResponse2);

    CatalogResponse catalogResponse2 =
        CatalogResponse.create("Catalog 2", "http://www.catalog2.it", resourceResponses2);

    List<CatalogResponse> catalogResponses = new ArrayList();
    catalogResponses.add(catalogResponse1);
    catalogResponses.add(catalogResponse2);

    CatalogsResponse catalogs = CatalogsResponse.create(catalogResponses);
    assertEquals(catalogs.getCatalogs(), catalogResponses);
  }

  @Test
  public void testCatalogsResponseMissingMandatoryField() {
    assertThrows(
        NullPointerException.class,
        () -> {
          CatalogsResponse.create(null);
        });
  }
}
