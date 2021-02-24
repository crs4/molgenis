package org.molgenis.api.ejprd.service.bbmri;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.molgenis.api.ejprd.service.QueryBuilder;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class BBMRIEricQueryBuilder extends QueryBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BBMRIEricQueryBuilder.class);
  private static final String EJPRD_BIOBANK_DATASET_TYPE_NAME = "BiobankDataset";
  private static final String EJPRD_PATIENT_REGISTRY_DATASET_TYPE_NAME = "PatientRegistryDataset";
  private static final String BBMRI_BIOBANK_DATASET_TYPE_NAME = "BIOBANK";
  private static final String BBMRI_PATIENT_REGISTRY_DATASET_TYPE_NAME = "REGISTRY";
  private static final String BIOBANK_ENTITY_ID = "eu_bbmri_eric_biobanks";

  private final DataService dataService;

  private List<String> biobankResources;

  public BBMRIEricQueryBuilder(DataService dataService) {
    this.dataService = requireNonNull(dataService);
  }

  @Override
  public Query<Entity> buildCount() {
    return getBaseQuery();
  }

  @Override
  public Query<Entity> build() {
    return getBaseQuery().pageSize(getPageSize()).offset(getOffset());
  }

  private String transcodeResourceType(String resourceType) {
    if (resourceType.equals(EJPRD_BIOBANK_DATASET_TYPE_NAME)) {
      return BBMRI_BIOBANK_DATASET_TYPE_NAME;
    } else if (resourceType.equals(EJPRD_PATIENT_REGISTRY_DATASET_TYPE_NAME)) {
      return BBMRI_PATIENT_REGISTRY_DATASET_TYPE_NAME;
    }
    return null;
  }

  private List<String> getBiobankResources(DataService dataService, List<String> resourceType) {

    Query<Entity> q = new QueryImpl<>();
    resourceType =
        resourceType.stream().map(this::transcodeResourceType).collect(Collectors.toList());
    q.in("ressource_types", resourceType);
    Stream<Entity> entities = dataService.findAll(BIOBANK_ENTITY_ID, q);
    List<String> biobankResources = new ArrayList<>();
    entities.forEach(
        entity -> {
          String biobankId = entity.getString("id");
          biobankResources.add(biobankId);
        });
    return biobankResources;
  }

  private Query<Entity> getBaseQuery() {
    List<String> diseaseCode = getDiseaseCode();

    Query<Entity> q = new QueryImpl<>();
    diseaseCode =
        diseaseCode.stream().map(dc -> String.format("ORPHA:%s", dc)).collect(Collectors.toList());
    LOG.info("Querying for orphacodes: {}", String.join(",", diseaseCode));
    q.nest();
    q.in("diagnosis_available.code", diseaseCode);
    if (anyOptionalParameter()) {
      if (getResourceType() != null) {
        if (biobankResources == null) {
          biobankResources = getBiobankResources(dataService, getResourceType());
        }
        q.and();
        q.in("biobank", biobankResources);
      }
      if (getCountry() != null) {
        q.and();
        q.in("country", getCountry());
      }
    }
    q.unnest();

    return q;
  }

  private boolean anyOptionalParameter() {
    return getResourceType() != null || getCountry() != null;
  }
}
