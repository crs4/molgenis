package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ResourceRequest;
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

@Controller
@RequestMapping(ResourceApiController.BASE_URI)
public class ResourceApiController implements ResourceApi {

  private static final Logger LOG = LoggerFactory.getLogger(EJPRDController.class);

  static final String BASE_URI = ApiNamespace.API_PATH + "/ejprd";

  private final DataService dataService;

  private static final String apiVersion = "v1";

  ResourceApiController(DataService dataService) {
    this.dataService = requireNonNull(dataService);
  }

  @GetMapping("/resource")
  @ResponseBody
  @RunAsSystem
  public DataResponse getResourceRequest(@Valid ResourceRequest resourceRequest) {

    // TODO: it should get the builder dynamically
    QueryBuilder queryBuilder = new BBMRIEricQueryBuilder();
    Query<Entity> q =
        queryBuilder.getQuery(
            resourceRequest.getOrphaCode(),
            BBMRIEricQueryBuilder.ORPHANET_ONTOLOGY, // TODO: this should not be static
            resourceRequest.getName());

    Stream<Entity> entities = dataService.findAll(queryBuilder.getEntityType(), q);

    List<ResourceResponse> resources = new ArrayList<>();
    Consumer<Entity> entityConsumer =
        collection -> {
          resources.add(createResource(collection));
        };
    entities.forEach(entityConsumer);

    return DataResponse.create(apiVersion, resources, null);
  }

  private ResourceResponse createResource(Entity entity) {
    ResourceAdapter mapper = new BBMRIEricResourceAdapter(entity);
    return mapper.createResource();
  }
}
