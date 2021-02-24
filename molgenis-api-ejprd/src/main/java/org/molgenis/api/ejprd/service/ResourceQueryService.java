package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ResourceRequest;

public interface ResourceQueryService {

  <T> T query(ResourceRequest queryParam);

  DataResponse getById(String resourceId);
}
