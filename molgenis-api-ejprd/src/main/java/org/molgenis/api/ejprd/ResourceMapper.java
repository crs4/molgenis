package org.molgenis.api.ejprd;

import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.data.Entity;

public interface ResourceMapper {

  ResourceResponse mapEntity(Entity entity);
}
