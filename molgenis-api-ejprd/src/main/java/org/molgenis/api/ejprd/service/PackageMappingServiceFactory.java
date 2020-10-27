package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.service.bbmri.BBMRIEricMappingServiceFactory;

public abstract class PackageMappingServiceFactory {

  public static PackageMappingServiceFactory getFactory() {
    return new BBMRIEricMappingServiceFactory();
  }

  public abstract QueryBuilder getQueryBuilder();

  public abstract ResourceMapper getResourceMapper();

  public abstract String getEntityTypeId();

  public abstract String getExternalSourcesEntityTypeId();
}
