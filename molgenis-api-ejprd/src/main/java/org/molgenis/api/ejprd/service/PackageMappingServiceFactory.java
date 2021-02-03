package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.service.bbmri.BBMRIEricMappingServiceFactory;
import org.molgenis.data.DataService;

public abstract class PackageMappingServiceFactory {

  public static PackageMappingServiceFactory getFactory(DataService dataService) {
    return new BBMRIEricMappingServiceFactory();
  }

  public static PackageMappingServiceFactory getFactory() {
    return new BBMRIEricMappingServiceFactory();
  }

  public abstract QueryBuilder getQueryBuilder();

  public abstract QueryBuilder getQueryBuilder(DataService dataService);

  public abstract ResourceMapper getResourceMapper();

  public abstract String getEntityTypeId();

  public abstract String getExternalSourcesEntityTypeId();
}
