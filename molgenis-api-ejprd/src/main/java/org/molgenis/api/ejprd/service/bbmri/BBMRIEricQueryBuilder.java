package org.molgenis.api.ejprd.service.bbmri;

import org.molgenis.api.ejprd.service.QueryBuilder;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BBMRIEricQueryBuilder extends QueryBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BBMRIEricQueryBuilder.class);

  @Override
  public Query<Entity> buildCount() {
    return getBaseQuery();
  }

  @Override
  public Query<Entity> build() {
    return getBaseQuery().pageSize(getPageSize()).offset(getOffset());
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
