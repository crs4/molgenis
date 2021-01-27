package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ExternalSourceQueryService implements ResourceQueryService {
  private static final Logger LOG = LoggerFactory.getLogger(InternalResourceQueryService.class);

  private String serviceBaseURL;
  private RestTemplate restTemplate = new RestTemplate();

  public ExternalSourceQueryService() {}

  public void setServiceBaseURL(String serviceBaseURL) {
    this.serviceBaseURL = serviceBaseURL;
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public <T> T query(
      String orphaCode, String type, String diseaseName, Integer skip, Integer limit) {
    String orphacCodeQp = String.format("orphaCode=%s", orphaCode);
    String skipQp = skip != null ? String.format("skip=%d", skip) : "";
    String limitQp = limit != null ? String.format("limit=%d", limit) : "";
    String queryParameters = String.join("&", orphacCodeQp, skipQp, limitQp);

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(String.format("%s?%s", serviceBaseURL, queryParameters));

    ResponseEntity<String> response;
    try {
      response = restTemplate.getForEntity(builder.toUriString(), String.class);
    } catch (ResourceAccessException ex) {
      return null;
    }

    // TODO: handle NullPointerException in case response.getBody() is malformed
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return (T) DataResponse.fromJson(response.getBody());
    }
    return (T) ErrorResponse.fromJson(response.getBody());
  }

  @Override
  public DataResponse getById(String resourceId) {
    return null;
  }
}
