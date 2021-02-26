package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.service.bbmri.BBMRIEricMappingServiceFactory;
import org.molgenis.data.DataService;

public abstract class PackageMappingServiceFactory {

  public static PackageMappingServiceFactory getFactory() {
    return new BBMRIEricMappingServiceFactory();
  }

  public abstract QueryBuilder getQueryBuilder(DataService dataService);

  public abstract ResourceMapper getResourceMapper();

  public abstract String getExternalSourcesEntityTypeId();
}
