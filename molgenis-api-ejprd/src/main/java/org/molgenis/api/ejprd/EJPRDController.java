package org.molgenis.api.ejprd;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.externalServices.ExternalSourceService;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.CatalogsResponse;
import org.molgenis.api.ejprd.model.ExternalResourceRequest;
import org.molgenis.data.DataService;
import org.molgenis.security.core.runas.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired private DataService dataService;

  private static UriComponentsBuilder getBaseUri() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_URI);
  }

  @GetMapping("/external_resource/")
  @ResponseBody
  @RunAsSystem
  public CatalogsResponse getExternalResource(@Valid ExternalResourceRequest request)
      throws Exception {

    ArrayList<String> externalResourcesList = request.getExternalSources();

    System.out.println(externalResourcesList);
    String diagnosisAvailable = request.getDiagnosisAvailable();
    List<CatalogResponse> catalogs = new ArrayList<>();

    // Todo: Move them in a Bean or globally in the controller
    ExternalSourceService es = new ExternalSourceService(dataService);
    HashMap<String, HashMap<String, String>> configuredExternalSources =
        es.getConfiguredExternalSources();

    if (externalResourcesList != null) {
      // Scan all requested external resources
      for (String resource : externalResourcesList) {
        System.out.println(resource);
        if (configuredExternalSources.keySet().contains(resource)) {
          HashMap matchingSource = configuredExternalSources.get(resource);
          String serviceURI = (String) matchingSource.get("service_uri");
          System.out.println(serviceURI);
          JsonObject externalResources = es.getExternalResources(serviceURI, "test");
          CatalogResponse cResponse =
              es.createExternalServiceCatalogReponse(matchingSource, externalResources);
          catalogs.add(cResponse);

        } else {
          throw new Exception("Unknown external resource identifier");
        }
      }
    }
    return CatalogsResponse.create(catalogs);
  }
}
