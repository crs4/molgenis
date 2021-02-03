package org.molgenis.api.ejprd.service.bbmri;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
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

  private DataService dataService;

  private ArrayList<String> biobankResources;

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

  public Query<Entity> buildLookupQuery() {
    return getLookupResourceTypeQuery();
  }

  private String transcodeResourceType(String resourceType) {
    if (resourceType.equals(EJPRD_BIOBANK_DATASET_TYPE_NAME)) {
      return BBMRI_BIOBANK_DATASET_TYPE_NAME;
    } else if (resourceType.equals(EJPRD_PATIENT_REGISTRY_DATASET_TYPE_NAME)) {
      return BBMRI_PATIENT_REGISTRY_DATASET_TYPE_NAME;
    }
    return null;
  }

  private ArrayList<String> getBiobankResources(DataService dataService, String resourceType) {
    Query<Entity> q = new QueryImpl<>();
    q.nest();
    q.eq("ressource_types", transcodeResourceType(resourceType));
    q.unnest();
    Stream<Entity> entities = dataService.findAll("eu_bbmri_eric_biobanks", q);
    ArrayList<String> biobankResources = new ArrayList();
    Iterator i = entities.iterator();
    while (i.hasNext()) {
      Entity e = (Entity) i.next();
      String biobankId = e.getString("id");
      biobankResources.add(biobankId);
    }
    return biobankResources;
  }

  private Query<Entity> getLookupResourceTypeQuery() {
    Query<Entity> q = new QueryImpl<>();
    q.nest();
    q.eq("ressourceTypes", transcodeResourceType(getResourceType()));
    q.unnest();
    return q;
  }

  private Query<Entity> getBaseQuery() {
    String diseaseCode = getDiseaseCode();
    String diseaseOntology = getDiseaseOntology();
    String diseaseName = getDiseaseName();

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
      if (getResourceType() != null) {
        if (biobankResources == null) {
          biobankResources = getBiobankResources(dataService, getResourceType());
        }
        q.and();
        q.in("biobank", biobankResources);
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
