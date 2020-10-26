package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.model.DataResponse;

public interface ResourceQueryService {

  DataResponse query(String orphaCode, String diseaseName, Integer skip, Integer limit);

  DataResponse getById(String resourceId);
}
