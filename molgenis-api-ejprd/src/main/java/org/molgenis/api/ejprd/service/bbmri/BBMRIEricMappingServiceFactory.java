package org.molgenis.api.ejprd.service.bbmri;

import org.molgenis.api.ejprd.service.PackageMappingServiceFactory;
import org.molgenis.api.ejprd.service.QueryBuilder;
import org.molgenis.api.ejprd.service.ResourceMapper;

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

  @Override
  public String getExternalSourcesEntityTypeId() {
    return "eu_bbmri_eric_external_sources";
  }
}
