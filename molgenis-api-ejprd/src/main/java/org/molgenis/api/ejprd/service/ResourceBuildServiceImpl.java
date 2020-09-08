package org.molgenis.api.ejprd.service;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.molgenis.api.ejprd.QueryBuilder;
import org.molgenis.api.ejprd.ResourceMapper;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.api.support.PageUtils;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResourceBuildServiceImpl implements ResourceBuildService {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceBuildServiceImpl.class);

  private final DataService dataService;

  private static final String apiVersion = "v1";
  private final PackageMappingServiceFactory packageMappingServiceFactory;

  public ResourceBuildServiceImpl(DataService dataService) {
    this.dataService = requireNonNull(dataService);
    this.packageMappingServiceFactory = PackageMappingServiceFactory.getFactory();
  }

  @Override
  public DataResponse build(String orphaCode, String diseaseName, Integer skip, Integer limit) {
    QueryBuilder queryBuilder =
        packageMappingServiceFactory
            .getQueryBuilder()
            .setDiseaseCode(orphaCode)
            .setDiseaseName(diseaseName)
            .setPageSize(limit)
            .setOffset(skip * limit);

    int totalCount = getTotalCount(queryBuilder.buildCount());
    Stream<Entity> entities = query(queryBuilder.build());

    List<ResourceResponse> resources = mapEntity(entities);

    return DataResponse.builder()
        .setApiVersion(apiVersion)
        .setPage(PageUtils.getPageResponse(limit, limit * skip, totalCount))
        .setResourceResponses(resources)
        .build();
  }

  private int getTotalCount(Query<Entity> query) {
    return Math.toIntExact(
        dataService.count(packageMappingServiceFactory.getEntityTypeId(), query));
  }

  private Stream<Entity> query(Query<Entity> query) {
    return dataService.findAll(packageMappingServiceFactory.getEntityTypeId(), query);
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
