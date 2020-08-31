package org.molgenis.api.ejprd;

import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BBMRIEricQueryBuilder extends QueryBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BBMRIEricQueryBuilder.class);

  @Override
  public Query<Entity> build() {
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
