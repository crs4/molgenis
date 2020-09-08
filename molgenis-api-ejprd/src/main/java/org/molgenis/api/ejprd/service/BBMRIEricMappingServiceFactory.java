package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.QueryBuilder;
import org.molgenis.api.ejprd.ResourceMapper;

public class BBMRIEricMappingServiceFactory extends PackageMappingServiceFactory {

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
