package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.molgenis.api.ApiNamespace;
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

  @GetMapping("/collection/")
  @ResponseBody
  @RunAsSystem
  public List<Resource> getResource() {
    // TODO: it should get the builder dinamically
    QueryBuilder queryBuilder = new BBMRIEricQueryBuilder();
    Query<Entity> q = queryBuilder.getQuery();

    Stream<Entity> entities = dataService.findAll(queryBuilder.getEntityType(), q);

    List<Resource> resources = new ArrayList<>();
    Consumer<Entity> entityConsumer =
        collection -> {
          resources.add(createResource(collection));
        };
    entities.forEach(entityConsumer);
    return resources;
  }

  private Resource createResource(Entity entity) {
    ResourceAdapter mapper = new BBMRIEricResourceAdapter(entity);
    return mapper.createResource();
  }
}
