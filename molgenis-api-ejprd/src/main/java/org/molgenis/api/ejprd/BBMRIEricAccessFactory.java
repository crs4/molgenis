package org.molgenis.api.ejprd;

public class BBMRIEricAccessFactory extends ResourceAccessFactory {

  @Override
  public QueryBuilder getQueryBuilder() {
    return new BBMRIEricQueryBuilder();
  }

  @Override
  public ResourceMapper getResourceMapper() {
    return new BBMRIEricResourceMapper();
  }

  @Override
  public String getEntityTypeId() {
    return "eu_bbmri_eric_collections";
  }
}
