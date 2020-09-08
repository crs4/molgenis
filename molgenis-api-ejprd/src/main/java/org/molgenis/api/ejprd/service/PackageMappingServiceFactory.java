package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.QueryBuilder;
import org.molgenis.api.ejprd.ResourceMapper;

public abstract class PackageMappingServiceFactory {

  public static PackageMappingServiceFactory getFactory() {
    return new BBMRIEricMappingServiceFactory();
  }

  public abstract QueryBuilder getQueryBuilder();

  public abstract ResourceMapper getResourceMapper();

  public abstract String getEntityTypeId();
}
