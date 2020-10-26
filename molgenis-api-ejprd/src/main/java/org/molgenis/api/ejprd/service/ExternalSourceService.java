package org.molgenis.api.ejprd.service;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.CatalogsResponse;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ExternalSourceService {
  private static final String NAME_COLUMN = "name";
  private static final String BASE_URI_COLUMN = "base_uri";
  private static final String SERVICE_URI_COLUMN = "service_uri";

  private final RestTemplate restTemplate = new RestTemplate();
  private final DataService dataService;
  private final PackageMappingServiceFactory packageMappingServiceFactory;

  public ExternalSourceService(DataService dataService) {
    this.dataService = requireNonNull(dataService);
    this.packageMappingServiceFactory = PackageMappingServiceFactory.getFactory();
  }

  private Entity findExternalSourceById(String externalSourceId) {
    return dataService.findOneById(
        packageMappingServiceFactory.getExternalSourcesEntityTypeId(), externalSourceId);
  }

  /**
   * Return the catalog with external resources from a specific Source
   *
   * @param externalSource: The Entity object of the External Source
   * @param orphaCode: The ORPHA code of the disease to look for in the external source
   * @return The CatalogResponse with the list of the resources found
   */
  public CatalogResponse getExternalResourcesFromSource(Entity externalSource, String orphaCode) {
    String serviceURI = externalSource.getString(SERVICE_URI_COLUMN);
    String catalogName = externalSource.getString(NAME_COLUMN);
    String catalogUrl = externalSource.getString(BASE_URI_COLUMN);

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(String.format("%s?orphaCode=%s", serviceURI, orphaCode));

    ResponseEntity<String> response;
    try {
      response = restTemplate.getForEntity(builder.toUriString(), String.class);
    } catch (ResourceAccessException ex) {
      return null;
    }

    if (response.getStatusCode().equals(HttpStatus.OK)) {
      DataResponse dataResponse = DataResponse.fromJson(response.getBody());
      return CatalogResponse.create(catalogName, catalogUrl, dataResponse.getResourceResponses());
    }
    return null;
  }

  /**
   * For very exernal source in input it queries the services and create a catalag response with
   * all the resources found
   * @param externalSourcesIds: The list of external sources ids
   * @param orphaCode: The ORPHA code to use to query the external source
   * @return a CatalogResponse with all the resources found for every source
   */
  public CatalogsResponse getExternalCatalogs(List<String> externalSourcesIds, String orphaCode) {
    List<CatalogResponse> catalogs = new ArrayList<>();

    for (String resource : externalSourcesIds) {
      Entity externalSource = findExternalSourceById(resource);
      if (externalSource != null) {
        CatalogResponse catalogResponse = getExternalResourcesFromSource(externalSource, orphaCode);
        if (catalogResponse != null) {
          catalogs.add(catalogResponse);
        }
      }
    }

    return CatalogsResponse.create(catalogs);
  }
}
