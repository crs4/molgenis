package org.molgenis.api.ejprd;

import org.molgenis.data.Entity;
import org.molgenis.data.Query;

public interface QueryBuilder {

  Query<Entity> getQuery();

  String getEntityType();
}
