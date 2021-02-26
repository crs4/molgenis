package org.molgenis.api.ejprd.service.bbmri;

import org.molgenis.api.ejprd.service.PackageMappingServiceFactory;
import org.molgenis.api.ejprd.service.QueryBuilder;
import org.molgenis.api.ejprd.service.ResourceMapper;
import org.molgenis.data.DataService;

public class BBMRIEricMappingServiceFactory extends PackageMappingServiceFactory {

  @Override
  public QueryBuilder getQueryBuilder(DataService dataService) {
    return new BBMRIEricQueryBuilder(dataService);
  }

  @Override
  public ResourceMapper getResourceMapper() {
    return new BBMRIEricResourceMapper();
  }

  @Override
  public String getExternalSourcesEntityTypeId() {
    return "eu_bbmri_eric_external_sources";
  }
}
