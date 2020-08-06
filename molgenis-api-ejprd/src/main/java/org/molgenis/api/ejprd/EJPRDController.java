package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
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
    List<ResourceResponse> resources = new ArrayList<>();
    String catalogName = "ERDRI";
    String catalogUrl = "https://eu-rd-platform.jrc.ec.europa.eu/erdridor/";
    ResourceResponse register =
        ResourceResponse.create(
            "Registry",
            "Banque Nationale de Données Maladies Rares",
            "https://eu-rd-platform.jrc.ec.europa.eu/erdridor/register/2444",
            "2444",
            "The French National Registry for Rare Diseases is a national tool for epidemiology and public health purposes in the field of rare diseases (RD). The data collection is mandatory for all the Rare Disease expert centers at the national level. A minimum data set (MDS) of about 60 items is collected for all the rare disease expert centers patients. This MDS strongly inspired the Common Data Elements (CDE) promoted by the EUCERD, and later by the JRC, which will greatly facilitate interoperability.");

    resources.add(register);
    CatalogResponse erdri = CatalogResponse.create(catalogName, catalogUrl, resources);
    List<CatalogResponse> catalogs = new ArrayList<>();
    catalogs.add(erdri);
    return CatalogsResponse.create(catalogs);
  }

  private ResourceResponse createResource(Entity entity) {
    ResourceAdapter mapper = new BBMRIEricResourceAdapter(entity);
    return mapper.createResource();
  }
}
