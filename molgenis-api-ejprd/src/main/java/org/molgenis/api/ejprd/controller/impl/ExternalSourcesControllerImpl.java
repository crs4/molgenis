package org.molgenis.api.ejprd.controller.impl;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.controller.ExternalSourcesController;
import org.molgenis.api.ejprd.exceptions.ExternalSourceNotFoundException;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ExternalResourceRequest;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.api.ejprd.service.ExternalResourceQueryService;
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

@Controller
@RequestMapping(ExternalSourcesControllerImpl.BASE_URI)
public class ExternalSourcesControllerImpl implements ExternalSourcesController {
  static final String BASE_URI = ApiNamespace.API_PATH + "/ejprd";

  private static final String NAME_COLUMN = "name";
  private static final String BASE_URI_COLUMN = "base_uri";
  private static final String SERVICE_URI_COLUMN = "service_uri";

  private static final Logger LOG = LoggerFactory.getLogger(ExternalSourcesControllerImpl.class);
  private final DataService dataService;
  private final PackageMappingServiceFactory packageMappingServiceFactory;
  private final ExternalResourceQueryService queryService;

  public ExternalSourcesControllerImpl(
      DataService dataService, ExternalResourceQueryService queryService) {
    this.dataService = requireNonNull(dataService);
    this.packageMappingServiceFactory = PackageMappingServiceFactory.getFactory();
    this.queryService = requireNonNull(queryService);
  }

  private Entity findExternalSourceById(String sourceId) {
    // TODO: consider moving external sources in a EJPRD package
    return dataService.findOneById(
        packageMappingServiceFactory.getExternalSourcesEntityTypeId(), sourceId);
  }

  @GetMapping("/external_sources/{sourceId}")
  @ResponseBody
  @RunAsSystem
  public CatalogResponse getExternalResource(
      @PathVariable("sourceId") String sourceId, @Valid ExternalResourceRequest request) {
    LOG.debug(String.format("Received %s", request.toString()));

    Entity source = findExternalSourceById(sourceId);

    if (source == null) {
      throw new ExternalSourceNotFoundException(
          HttpStatus.NOT_FOUND, String.format("External source %s not found", sourceId));
    }

    String serviceBaseUrl = source.getString(SERVICE_URI_COLUMN);
    String catalogName = source.getString(NAME_COLUMN);
    String catalogUrl = source.getString(BASE_URI_COLUMN);

    queryService.setServiceBaseURL(serviceBaseUrl);

    DataResponse response = queryService.query(request);

    List<ResourceResponse> resourceResponses =
        response != null ? response.getResourceResponses() : Collections.emptyList();
    PageResponse page = response != null ? response.getPage() : null;
    return CatalogResponse.create(catalogName, catalogUrl, resourceResponses, page);
  }
}
