package org.molgenis.api.ejprd.service.bbmri;

import org.molgenis.api.ejprd.service.QueryBuilder;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BBMRIEricQueryBuilder extends QueryBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BBMRIEricQueryBuilder.class);
  private static final String EJPRD_BIOBANK_DATASET_TYPE_NAME = "BiobankDataset";
  private static final String EJPRD_PATIENT_REGISTRY_DATASET_TYPE_NAME = "PatientRegistryDataset";
  private static final String BBMRI_BIOBANK_DATASET_TYPE_NAME = "BIOBANK";
  private static final String BBMRI_PATIENT_REGISTRY_DATASET_TYPE_NAME = "REGISTRY";

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

  private Query<Entity> getBaseQuery() {
    String diseaseCode = getDiseaseCode();
    String diseaseOntology = getDiseaseOntology();
    String diseaseName = getDiseaseName();
    String resourceType = getResourceType();

    if (diseaseCode == null) {
      diseaseOntology = null;
    } else {
      diseaseOntology = "orphanet";
      diseaseCode = String.format("ORPHA:%s", diseaseCode);
    }

    Query<Entity> q = new QueryImpl<>();
    if (diseaseCode != null) {
      q.nest();
      q.eq("diagnosis_available.code", diseaseCode);
      q.and();
      q.eq("diagnosis_available.ontology", diseaseOntology);
      if (resourceType != null) {
        q.and();
        q.eq("biobank.ressource_types", transcodeResourceType(resourceType));
      }
      q.unnest();
    }

    if (diseaseName != null) {
      if (q.getRules().size() != 0) {
        q.or();
      }
      q.like("diagnosis_available.label", diseaseName);
    }

    return q;
  }
}
