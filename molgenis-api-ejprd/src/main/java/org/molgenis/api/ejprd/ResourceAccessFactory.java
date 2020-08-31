package org.molgenis.api.ejprd;

public abstract class ResourceAccessFactory {

  public static ResourceAccessFactory getFactory() {
    return new BBMRIEricAccessFactory();
  }

  public abstract QueryBuilder getQueryBuilder();

  public abstract ResourceMapper getResourceMapper();

  public abstract String getEntityTypeId();
}
