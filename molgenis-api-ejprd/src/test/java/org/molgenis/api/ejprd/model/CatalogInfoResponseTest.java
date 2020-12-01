package org.molgenis.api.ejprd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CatalogInfoResponseTest {

  @Test
  public void testCatalogInfoResponseComplete(){
    Organization org =
        Organization.create(
            "ORG",
            "Orgnanization",
            "This is the ORG organization",
            "https://org.eu",
            null);

    CatalogInfoResponse catalog =
        CatalogInfoResponse.create(
            "ORG_Catalog",
            new ArrayList(Arrays.asList("CatalogOfRegistries", "CatalogOfBiobanks")),
            "Organization ORG Catalog",
            "This is the ORG Catalog",
            "https://org-catalog.eu",
            org,
            "v0.2",
            new ArrayList(
                Arrays.asList(
                    "/ will return endpoint information",
                    "/resource/search?orphaCode={orphacode} will return results based on the specified {orphacode}")));

    assertEquals(catalog.getId(), "ORG_Catalog");
    assertEquals(catalog.getOrganization(), org);

  }

  @Test
  public void testMandatoryFieldsOnly(){
    CatalogInfoResponse catalog =
        CatalogInfoResponse.create(
            "ORG_Catalog",
            new ArrayList(Arrays.asList("CatalogOfRegistries", "CatalogOfBiobanks")),
            null,
            null,
            null,
            null,
            null,
            null
        );

    assertEquals(catalog.getId(), "ORG_Catalog");
    assertEquals(catalog.getType(), new ArrayList(Arrays.asList("CatalogOfRegistries", "CatalogOfBiobanks")));

  }

  @Test
  public void testMissingMandatoryFields(){

    Organization org =
        Organization.create(
            "ORG",
            "Orgnanization",
            "This is the ORG organization",
            "https://org.eu",
            null);

    assertThrows(
        NullPointerException.class,
        () -> {
          CatalogInfoResponse catalog =
              CatalogInfoResponse.create(
                  null,
                  new ArrayList(Arrays.asList("CatalogOfRegistries", "CatalogOfBiobanks")),
                  "Organization ORG Catalog",
                  "This is the ORG Catalog",
                  "https://org-catalog.eu",
                  org,
                  "v0.2",
                  new ArrayList(
                      Arrays.asList(
                          "/ will return endpoint information",
                          "/resource/search?orphaCode={orphacode} will return results based on the specified {orphacode}")));

        });

    assertThrows(
        NullPointerException.class,
        () -> {
          CatalogInfoResponse catalog =
              CatalogInfoResponse.create(
                  "ORG_Catalog",
                  null,
                  "Organization ORG Catalog",
                  "This is the ORG Catalog",
                  "https://org-catalog.eu",
                  org,
                  "v0.2",
                  new ArrayList(
                      Arrays.asList(
                          "/ will return endpoint information",
                          "/resource/search?orphaCode={orphacode} will return results based on the specified {orphacode}")));

        });
  }
}
