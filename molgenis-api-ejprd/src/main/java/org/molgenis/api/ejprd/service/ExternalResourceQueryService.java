package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ErrorResponse;
import org.molgenis.api.ejprd.model.ResourceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ExternalResourceQueryService implements ResourceQueryService {
  private static final Logger LOG = LoggerFactory.getLogger(InternalResourceQueryService.class);

  private String serviceBaseURL;
  private RestTemplate restTemplate = new RestTemplate();

  public ExternalResourceQueryService() {}

  public void setServiceBaseURL(String serviceBaseURL) {
    this.serviceBaseURL = serviceBaseURL;
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public <T> T query(ResourceRequest queryParam) {
    String orphacCodeParameter =
        String.format("orphaCode=%s", String.join(",", queryParam.getOrphaCode()));
    String typeParameter =
        queryParam.getResourceType() != null
            ? String.format("resourceType=%s", String.join(",", queryParam.getResourceType()))
            : "";
    String countryParameter =
        queryParam.getCountry() != null
            ? String.format("country=%s", String.join(",", queryParam.getCountry()))
            : "";
    String skipParameter =
        queryParam.getSkip() != null ? String.format("skip=%d", queryParam.getSkip()) : "";
    String limitParameter =
        queryParam.getLimit() != null ? String.format("limit=%d", queryParam.getLimit()) : "";
    String queryParameters =
        String.join(
            "&",
            orphacCodeParameter,
            typeParameter,
            countryParameter,
            skipParameter,
            limitParameter);

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(String.format("%s?%s", serviceBaseURL, queryParameters));

    LOG.debug(String.format("Querying external source: %s", builder.toUriString()));
    ResponseEntity<String> response;
    LOG.debug(String.format(builder.toUriString(), String.class));
    String s = builder.toUriString();
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
