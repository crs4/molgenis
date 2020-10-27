package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.CatalogsResponse;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ExternalResourceRequest;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.api.ejprd.service.ExternalSourceQueryService;
import org.molgenis.api.ejprd.service.PackageMappingServiceFactory;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.security.core.runas.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(ExternalResourcesController.BASE_URI)
public class ExternalResourcesController {
  static final String BASE_URI = ApiNamespace.API_PATH + "/ejprd";
  private static final String NAME_COLUMN = "name";
  private static final String BASE_URI_COLUMN = "base_uri";
  private static final String SERVICE_URI_COLUMN = "service_uri";

  private static final Logger LOG = LoggerFactory.getLogger(ExternalResourcesController.class);
  private final DataService dataService;
  private final PackageMappingServiceFactory packageMappingServiceFactory;

  ExternalResourcesController(DataService dataService) {
    this.dataService = requireNonNull(dataService);
    this.packageMappingServiceFactory = PackageMappingServiceFactory.getFactory();
  }

  private static UriComponentsBuilder getBaseUri() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_URI);
  }

  private Entity findExternalSourceById(String sourceId) {
    return dataService.findOneById(
        packageMappingServiceFactory.getExternalSourcesEntityTypeId(), sourceId);
  }

  @GetMapping("/external_resource/")
  @ResponseBody
  @RunAsSystem
  public CatalogsResponse getExternalResource(@Valid ExternalResourceRequest request) {
    ArrayList<String> externalSourcesIds = request.getExternalSources();

    // Todo: enable diagnosisAvailable filter in the future
    String diagnosisAvailable = request.getDiagnosisAvailable();
    String orphaCode = diagnosisAvailable;

    if (diagnosisAvailable.contains("ORPHA:")) {
      orphaCode = diagnosisAvailable.split(":")[1];
    }
    List<CatalogResponse> catalogs = new ArrayList<>();

    for (String sourceId : externalSourcesIds) {
      Entity source = findExternalSourceById(sourceId);

      String serviceBaseUrl = source.getString(SERVICE_URI_COLUMN);
      String catalogName = source.getString(NAME_COLUMN);
      String catalogUrl = source.getString(BASE_URI_COLUMN);

      ExternalSourceQueryService queryService = new ExternalSourceQueryService(serviceBaseUrl);

      DataResponse response = queryService.query(orphaCode, null, null, null);
      List<ResourceResponse> resourceResponses =
          response != null ? response.getResourceResponses() : Collections.emptyList();
      CatalogResponse catalogResponse =
          CatalogResponse.create(catalogName, catalogUrl, resourceResponses);
      if (catalogResponse != null) {
        catalogs.add(catalogResponse);
      }
    }

    return CatalogsResponse.create(catalogs);
  }
}
