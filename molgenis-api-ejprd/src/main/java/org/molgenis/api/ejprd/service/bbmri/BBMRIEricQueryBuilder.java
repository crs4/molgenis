package org.molgenis.api.ejprd.service.bbmri;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

  private static final String COLLECTION_ENTITY_ID = "eu_bbmri_eric_collections";
  private static final String BIOBANK_ENTITY_ID = "eu_bbmri_eric_biobanks";
  private static final String[] RESOURCE_TYPES = {"BiobankDataset", "PatientRegistryDataset"};

  private static final String ORPHA_CODE_EXACT_MATCHING_COLUMN = "orphanet_exact_matching";
  private static final String ORPHA_CODE_LOOKUP_ENTITY = "eu_bbmri_eric_disease_types";

  private final DataService dataService;

  private Query<Entity> query;

  public BBMRIEricQueryBuilder(DataService dataService) {
    this.dataService = requireNonNull(dataService);
  }

  @Override
  public Query<Entity> buildCount() {
    return getBaseQuery();
  }

  @Override
  public Query<Entity> build() {
    if (getBaseQuery() != null) {
      return getBaseQuery().pageSize(getPageSize()).offset(getOffset());
    }
    return null;
  }

  private String transcodeResourceType(String resourceType) {
    if (resourceType.equals(EJPRD_BIOBANK_DATASET_TYPE_NAME)) {
      return BBMRI_BIOBANK_DATASET_TYPE_NAME;
    } else if (resourceType.equals(EJPRD_PATIENT_REGISTRY_DATASET_TYPE_NAME)) {
      return BBMRI_PATIENT_REGISTRY_DATASET_TYPE_NAME;
    }
    return null;
  }

  private List<Entity> getBiobankResources(List<String> resourceType) {
    List<String> transcodedResourceType =
        resourceType.stream().map(this::transcodeResourceType).collect(Collectors.toList());

    Query<Entity> q = new QueryImpl<>(dataService, BIOBANK_ENTITY_ID);
    q.in("ressource_types", transcodedResourceType);
    return q.findAll().collect(Collectors.toList());
  }

  public List<Entity> getICD10ExactMatchByOrphaCode(List<String> orphaCodes) {
    Query<Entity> q = new QueryImpl<>(dataService, ORPHA_CODE_LOOKUP_ENTITY);
    q.in(ORPHA_CODE_EXACT_MATCHING_COLUMN, orphaCodes);
    // Stream<Entity> entities = q.findAll();
    return q.findAll().collect(Collectors.toList());
  }

  private Query<Entity> getBaseQuery() {

    if (this.query == null) {

      List<String> diseaseCode = getDiseaseCode();
      String name = getName();

      query = new QueryImpl<>(dataService, COLLECTION_ENTITY_ID);
      // Check if the orphaCode and name fields are both present, or only one of them
      query.nest();

      if (diseaseCode != null && !diseaseCode.isEmpty()) {
        diseaseCode =
            diseaseCode.stream()
                .map(dc -> String.format("ORPHA:%s", dc))
                .collect(Collectors.toList());
        LOG.info("Querying for orphacodes: {}", String.join(",", diseaseCode));
        // Obtain matching ICD-10 codes from the service
        List<Entity> matchingICD10Codes = getICD10ExactMatchByOrphaCode(diseaseCode);
        if (matchingICD10Codes.isEmpty()) {
          return query;
        }
        query.in("diagnosis_available.id", matchingICD10Codes);
      }
      if (name != null && !name.isEmpty()) {
        if (diseaseCode != null && !diseaseCode.isEmpty()) {
          query.and();
        }
        query.like("name", getName());
      }

      if (anyOptionalParameter()) {
        if (getResourceType() != null
            && !getResourceType()
                .containsAll(Arrays.stream(RESOURCE_TYPES).collect(Collectors.toList()))) {
          List<Entity> biobankResources = getBiobankResources(getResourceType());
          query.and();
          query.in("biobank", biobankResources);
        }
        if (getCountry() != null) {
          query.and();
          query.in("country", getCountry());
        }
      }

      query.unnest();
    }
    return query;
  }

  private boolean anyOptionalParameter() {
    return getResourceType() != null || getCountry() != null;
  }
}
