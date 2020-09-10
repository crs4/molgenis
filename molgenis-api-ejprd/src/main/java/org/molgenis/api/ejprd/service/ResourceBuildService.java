package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.model.DataResponse;

public interface ResourceBuildService {

  DataResponse build(String orphaCode, String diseaseName, Integer skip, Integer limit);

  DataResponse build(String resourceId);
}
