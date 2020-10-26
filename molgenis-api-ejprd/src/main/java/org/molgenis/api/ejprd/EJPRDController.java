package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.model.CatalogsResponse;
import org.molgenis.api.ejprd.model.ExternalResourceRequest;
import org.molgenis.api.ejprd.service.ExternalSourceService;
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
@RequestMapping(EJPRDController.BASE_URI)
public class EJPRDController {
  static final String BASE_URI = ApiNamespace.API_PATH + "/ejprd";
  private static final Logger LOG = LoggerFactory.getLogger(EJPRDController.class);

  private final ExternalSourceService externalSourceService;

  EJPRDController(ExternalSourceService externalSourceService) {
    this.externalSourceService = requireNonNull(externalSourceService);
  }

  private static UriComponentsBuilder getBaseUri() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_URI);
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
    return externalSourceService.getExternalCatalogs(externalSourcesIds, orphaCode);
  }
}
