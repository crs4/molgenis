package org.molgenis.api.ejprd;

import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BBMRIEricQueryBuilder implements QueryBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BBMRIEricQueryBuilder.class);

  public static final String ORPHANET_ONTOLOGY = "orphanet";

  private static final String entityType = "eu_bbmri_eric_collections";

  @Override
  public Query<Entity> getQuery(String diseaseCode, String diseaseOntology, String diseaseName) {
    if (diseaseCode == null) {
      diseaseOntology = null;
    }

    if (diseaseOntology != null && diseaseOntology.equals(ORPHANET_ONTOLOGY)) {
      diseaseCode = String.format("ORPHA:%s", diseaseCode);
    }

    Query<Entity> q = new QueryImpl<>();
    if (diseaseCode != null && diseaseOntology != null) {
      q.nest();
      q.eq("diagnosis_available.code", diseaseCode);
      q.and();
      q.eq("diagnosis_available.ontology", diseaseOntology);
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

  @Override
  public String getEntityType() {
    return entityType;
  }
}
