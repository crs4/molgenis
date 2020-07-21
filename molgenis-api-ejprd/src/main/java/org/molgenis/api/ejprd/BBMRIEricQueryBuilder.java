package org.molgenis.api.ejprd;

import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;

public class BBMRIEricQueryBuilder implements QueryBuilder {

  private static final String entityType = "eu_bbmri_eric_collections";

  @Override
  public Query<Entity> getQuery() {
    Query<Entity> q = new QueryImpl<>();
    q.nest();
    q.eq("diagnosis_available.code", "R55");
    q.and();
    q.eq("diagnosis_available.ontology", "ICD-10");
    q.unnest();
    return q;
  }

  @Override
  public String getEntityType() {
    return entityType;
  }
}
