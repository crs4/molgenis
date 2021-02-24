package org.molgenis.api.ejprd.controller.impl;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.controller.ResourceController;
import org.molgenis.api.ejprd.model.CatalogInfoResponse;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.Organisation;
import org.molgenis.api.ejprd.model.ResourceRequest;
import org.molgenis.api.ejprd.service.InternalResourceQueryService;
import org.molgenis.security.core.runas.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(ResourceControllerImpl.BASE_URI)
public class ResourceControllerImpl implements ResourceController {

  static final String BASE_URI = ApiNamespace.API_PATH + "/ejprd";
  private static final Logger LOG = LoggerFactory.getLogger(ResourceControllerImpl.class);
  private static final String apiVersion = "v0.2";
  private final InternalResourceQueryService resourceQueryService;

  public ResourceControllerImpl(InternalResourceQueryService resourceQueryService) {
    this.resourceQueryService = requireNonNull(resourceQueryService);
  }

  @GetMapping("/")
  @ResponseBody
  @RunAsSystem
  public CatalogInfoResponse getCatalogInfo() {

    Organisation bbmri =
        Organisation.create(
            "BBMRI.ERIC",
            "BBMRI-ERIC Orgnanization",
            "European research infrastructure for biobanking",
            "https://www.bbmri-eric.eu",
            null);

    return CatalogInfoResponse.create(
        "BBMRI-ERIC-Catalog",
        new ArrayList<>(Arrays.asList("CatalogOfRegistries", "CatalogOfBiobanks")),
        "BBMRI ERIC Catalog",
        "BBMRI Eric Europen Catalog of Biobanks and Registries",
        "https://www.bbmri-eric.eu/services/directory/",
        bbmri,
        "v0.2",
        new ArrayList<>(
            Arrays.asList(
                "/ will return endpoint information",
                "/resource/search?orphaCode={orphacode} will return results based on the specified {orphacode}")));
  }

  @GetMapping("/resource/search")
  @ResponseBody
  @RunAsSystem
  public DataResponse getResourceRequest(@Valid ResourceRequest resourceRequest) {
    LOG.info("Received query request: {}", resourceRequest);
    return resourceQueryService.query(resourceRequest);
  }

  @GetMapping("/resource/{resourceId}")
  @ResponseBody
  @RunAsSystem
  public DataResponse getResourceById(@PathVariable("resourceId") String resourceId) {
    LOG.info("Received get request: for resource {}", resourceId);
    return resourceQueryService.getById(resourceId);
  }
}
