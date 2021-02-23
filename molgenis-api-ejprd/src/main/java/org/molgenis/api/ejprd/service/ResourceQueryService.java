package org.molgenis.api.ejprd.service;

import java.util.List;
import org.molgenis.api.ejprd.model.DataResponse;

public interface ResourceQueryService {

  <T> T query(
      List<String> orphaCode,
      List<String> resourceType,
      List<String> country,
      String diseaseName,
      Integer skip,
      Integer limit);

  DataResponse getById(String resourceId);
}
