package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.CatalogsResponse;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
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
  private static final Logger LOG = LoggerFactory.getLogger(EJPRDController.class);

  static final String BASE_URI = ApiNamespace.API_PATH + "/ejprd";

  private final DataService dataService;

  EJPRDController(DataService dataService) {
    this.dataService = requireNonNull(dataService);
  }

  private static UriComponentsBuilder getBaseUri() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_URI);
  }

  @GetMapping("/query")
  @ResponseBody
  @RunAsSystem
  public List<ResourceResponse> getResource() {
    // TODO: it should get the builder dinamically
    QueryBuilder queryBuilder = new BBMRIEricQueryBuilder();
    Query<Entity> q = queryBuilder.getQuery();

    Stream<Entity> entities = dataService.findAll(queryBuilder.getEntityType(), q);

    List<ResourceResponse> resources = new ArrayList<>();
    Consumer<Entity> entityConsumer =
        collection -> {
          resources.add(createResource(collection));
        };
    entities.forEach(entityConsumer);
    return resources;
  }

  @GetMapping("/external_resource/")
  @ResponseBody
  @RunAsSystem
  public CatalogsResponse getExternalResource() {
    List<ResourceResponse> erdriResources = new ArrayList<>();

    String erdriCatalog = "ERDRI";
    String erdriUrl = "https://eu-rd-platform.jrc.ec.europa.eu/erdridor/";

    ResourceResponse register1 =
        ResourceResponse.create(
            "Registry",
            "Banque Nationale de Données Maladies Rares",
            "https://eu-rd-platform.jrc.ec.europa.eu/erdridor/register/2444",
            "2444",
            "The French National Registry for Rare Diseases is a national tool for epidemiology and public health purposes in the field of rare diseases (RD). The data collection is mandatory for all the Rare Disease expert centers at the national level. A minimum data set (MDS) of about 60 items is collected for all the rare disease expert centers patients. This MDS strongly inspired the Common Data Elements (CDE) promoted by the EUCERD, and later by the JRC, which will greatly facilitate interoperability.");
    ResourceResponse register2 =
        ResourceResponse.create(
            "Registry",
            "Degos Disease Registry (Registry for Malignant Atrophic Papulosis)",
            "https://eu-rd-platform.jrc.ec.europa.eu/erdridor/register/4607",
            "Degos-Disease",
            "Purpose of the registry is the support of research on demographics, epidemiology, prognosis, etiology and treatment of the disease Malignant Atrophic Papulosis (Köhlmeier-Degos disease, Degos disease)");
    erdriResources.add(register1);
    erdriResources.add(register2);
    CatalogResponse erdri = CatalogResponse.create(erdriCatalog, erdriUrl, erdriResources);

    String orphanetName = "Orphanet";
    String orphanetUrl = "https://www.orpha.net/";
    ResourceResponse register3 =
        ResourceResponse.create(
            "Biobank",
            "Cell line and DNA Biobank from patients affected by genetic diseases",
            "https://www.orpha.net/consor/cgi-bin/ResearchTrials_RegistriesMaterials.php?lng=EN&data_id=46305&RegistryMaterialName=Biobanca-di-linee-cellulari-e-di-DNA-da-pazienti-affetti-da-malattie-genetiche&title=Biobanca%20di%20linee%20cellulari%20e%20di%20DNA%20da%20pazienti%20affetti%20da%20malattie%20genetiche&search=ResearchTrials_RegistriesMaterials_Simple",
            "46305",
            null);
    CatalogResponse orphanet =
        CatalogResponse.create(orphanetName, orphanetUrl, Collections.singletonList(register3));

    List<CatalogResponse> catalogs = new ArrayList<>();
    catalogs.add(erdri);
    catalogs.add(orphanet);
    return CatalogsResponse.create(catalogs);
  }

  private ResourceResponse createResource(Entity entity) {
    ResourceAdapter mapper = new BBMRIEricResourceAdapter(entity);
    return mapper.createResource();
  }
}
