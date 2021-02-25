package org.molgenis.api.ejprd.service;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.InternalResourceRequest;
import org.molgenis.api.ejprd.model.ResourceRequest;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.api.support.PageUtils;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InternalResourceQueryService implements ResourceQueryService {

  private static final Logger LOG = LoggerFactory.getLogger(InternalResourceQueryService.class);
  private static final String apiVersion = "v0.2";
  private final DataService dataService;
  private final PackageMappingServiceFactory packageMappingServiceFactory;

  public InternalResourceQueryService(DataService dataService) {
    this.dataService = requireNonNull(dataService);
    this.packageMappingServiceFactory = PackageMappingServiceFactory.getFactory();
  }

  @Override
  public <T> T query(ResourceRequest queryParam) {
    InternalResourceRequest internalQueryParam = (InternalResourceRequest) queryParam;
    QueryBuilder queryBuilder =
        packageMappingServiceFactory
            .getQueryBuilder(dataService)
            .setDiseaseCode(internalQueryParam.getOrphaCode())
            .setResourceType(internalQueryParam.getResourceType())
            .setCountry(internalQueryParam.getCountry())
            .setPageSize(internalQueryParam.getLimit())
            .setOffset(internalQueryParam.getSkip() * internalQueryParam.getLimit());

    // if type is not null, we have to perform a Lookup on the Biobanks entity and retrieve
    // the list of all the entities matching that type

    int totalCount = getTotalCount(queryBuilder.buildCount());
    Stream<Entity> entities = query(queryBuilder.build());

    List<ResourceResponse> resources = mapEntity(entities);

    return (T)
        DataResponse.builder()
            .setApiVersion(apiVersion)
            .setPage(
                PageUtils.getPageResponse(
                    queryParam.getLimit(),
                    queryParam.getLimit() * queryParam.getSkip(),
                    totalCount))
            .setResourceResponses(resources)
            .build();
  }

  @Override
  public DataResponse getById(String resourceId) {
    EntityType entityType =
        dataService.getEntityType(packageMappingServiceFactory.getEntityTypeId());
    Object id = EntityUtils.getTypedValue(resourceId, entityType.getIdAttribute());
    Entity entity =
        this.dataService.findOneById(packageMappingServiceFactory.getEntityTypeId(), id);

    ResourceResponse resourceResponse = mapEntity(entity);
    return DataResponse.builder()
        .setApiVersion(apiVersion)
        .setPage(PageUtils.getPageResponse(1, 0, 1))
        .setResourceResponses(Collections.singletonList(resourceResponse))
        .build();
  }

  private int getTotalCount(Query<Entity> query) {
    return Math.toIntExact(
        dataService.count(packageMappingServiceFactory.getEntityTypeId(), query));
  }

  private Stream<Entity> query(Query<Entity> query) {
    return dataService.findAll(packageMappingServiceFactory.getEntityTypeId(), query);
  }

  private ResourceResponse mapEntity(Entity entity) {
    ResourceMapper mapper = packageMappingServiceFactory.getResourceMapper();
    return mapper.mapEntity(entity);
  }

  private List<ResourceResponse> mapEntity(Stream<Entity> entities) {
    ResourceMapper mapper = packageMappingServiceFactory.getResourceMapper();
    List<ResourceResponse> resources = new ArrayList<>();
    Consumer<Entity> entityConsumer =
        collection -> {
          resources.add(mapper.mapEntity(collection));
        };
    entities.forEach(entityConsumer);
    return resources;
  }
}
