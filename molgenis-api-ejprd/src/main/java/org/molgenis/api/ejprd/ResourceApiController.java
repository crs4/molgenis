package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.DataResponse.Page;
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

    Integer skip = resourceRequest.getSkip();
    Integer limit = resourceRequest.getLimit();

    ResourceAccessFactory factory = ResourceAccessFactory.getFactory();

    QueryBuilder queryBuilder = factory.getQueryBuilder();
    Query<Entity> q =
        queryBuilder
            .setDiseaseCode(resourceRequest.getOrphaCode())
            .setDiseaseName(resourceRequest.getName())
            .build();

    q.pageSize(limit);
    q.offset(limit * skip);

    Stream<Entity> entities = dataService.findAll(factory.getEntityTypeId(), q);

    ResourceMapper mapper = factory.getResourceMapper();
    List<ResourceResponse> resources = new ArrayList<>();
    Consumer<Entity> entityConsumer =
        collection -> {
          resources.add(mapper.mapEntity(collection));
        };
    entities.forEach(entityConsumer);

    return DataResponse.builder()
        .setApiVersion(apiVersion)
        .setPage(Page.create(limit * skip, resources.size(), limit))
        .setResourceResponses(resources)
        .build();
  }
}
