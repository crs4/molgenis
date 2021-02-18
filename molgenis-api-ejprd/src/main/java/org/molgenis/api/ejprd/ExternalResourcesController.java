package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.exceptions.ExternalSourceNotFoundException;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ExternalResourceRequest;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.api.ejprd.service.ExternalSourceQueryService;
import org.molgenis.api.ejprd.service.PackageMappingServiceFactory;
import org.molgenis.api.model.response.PageResponse;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.security.core.runas.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  private final ExternalSourceQueryService queryService;

  ExternalResourcesController(DataService dataService, ExternalSourceQueryService queryService) {
    this.dataService = requireNonNull(dataService);
    this.packageMappingServiceFactory = PackageMappingServiceFactory.getFactory();
    this.queryService = requireNonNull(queryService);
  }

  private static UriComponentsBuilder getBaseUri() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_URI);
  }

  private Entity findExternalSourceById(String sourceId) {
    return dataService.findOneById(
        packageMappingServiceFactory.getExternalSourcesEntityTypeId(), sourceId);
  }

  @GetMapping("/external_sources/{sourceId}")
  @ResponseBody
  @RunAsSystem
  public CatalogResponse getExternalResource(
      @PathVariable("sourceId") String sourceId, @Valid ExternalResourceRequest request) {
    LOG.debug(String.format("Received %s", request.toString()));

    List<String> orphaCode =
        request.getDiagnosisAvailable().stream()
            .map(
                oc -> {
                  if (oc.contains("ORPHA:")) {
                    return oc.split(":")[1];
                  }
                  return oc;
                })
            .collect(Collectors.toList());

    List<String> resourceType = request.getResourceType();
    Integer skip = request.getSkip();
    Integer limit = request.getLimit();

    Entity source = findExternalSourceById(sourceId);

    if (source == null) {
      throw new ExternalSourceNotFoundException(
          HttpStatus.NOT_FOUND, String.format("External source %s not found", sourceId));
    }

    String serviceBaseUrl = source.getString(SERVICE_URI_COLUMN);
    String catalogName = source.getString(NAME_COLUMN);
    String catalogUrl = source.getString(BASE_URI_COLUMN);

    queryService.setServiceBaseURL(serviceBaseUrl);

    DataResponse response = queryService.query(orphaCode, resourceType, null, skip, limit);

    List<ResourceResponse> resourceResponses =
        response != null ? response.getResourceResponses() : Collections.emptyList();
    PageResponse page = response != null ? response.getPage() : null;
    return CatalogResponse.create(catalogName, catalogUrl, resourceResponses, page);
  }
}
