package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.model.DataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ExternalSourceQueryService implements ResourceQueryService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final String serviceBaseURL;

  public ExternalSourceQueryService(String serviceBaseURL) {
    this.serviceBaseURL = serviceBaseURL;
  }

  @Override
  public DataResponse query(String orphaCode, String diseaseName, Integer skip, Integer limit) {
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(
            String.format("%s?orphaCode=%s", serviceBaseURL, orphaCode));

    ResponseEntity<String> response;
    try {
      response = restTemplate.getForEntity(builder.toUriString(), String.class);
    } catch (ResourceAccessException ex) {
      return null;
    }

    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return DataResponse.fromJson(response.getBody());
    }
    return null;
  }

  @Override
  public DataResponse getById(String resourceId) {
    return null;
  }
}
