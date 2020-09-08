package org.molgenis.api.ejprd;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.externalServices.ERDRIResourcesService;
import org.molgenis.api.ejprd.externalServices.OrphanetResourcesService;
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
  private static final String ERDRIIdentifier = "erdri";
  private static final String orphanetIdentifier = "orphanet";
  private static final String rdConnectIdentifier = "rd-connect";
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
    String diagnosisAvailable = request.getDiagnosisAvailable();
    List<CatalogResponse> catalogs = new ArrayList<>();

    if (externalResourcesList != null) {
      // Scan all requested external resources
      for (String resource : externalResourcesList) {
        if (resource.equals(ERDRIIdentifier)) {
          ERDRIResourcesService es = new ERDRIResourcesService(this.dataService);
          JsonObject ERDRIRes = es.getERDRIResources("test"); // this is a fake param value by now
          CatalogResponse erdri = es.createERDRICatalogReponse(ERDRIRes);
          catalogs.add(erdri);
        } else if (resource.equals(orphanetIdentifier)) {
          OrphanetResourcesService os = new OrphanetResourcesService();
          CatalogResponse orphanet = os.createOrphanetCatalogReponse();
          catalogs.add(orphanet);
        } else {
          throw new Exception("Unknown external resource identifier");
        }
      }
    }
    return CatalogsResponse.create(catalogs);
  }
}
