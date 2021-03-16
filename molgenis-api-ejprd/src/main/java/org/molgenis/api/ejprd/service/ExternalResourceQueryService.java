package org.molgenis.api.ejprd.service;

import java.util.List;
import java.util.stream.Collectors;
import org.molgenis.api.ejprd.exceptions.ExternalSourceErrorException;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ExternalResourceRequest;
import org.molgenis.api.ejprd.model.ResourceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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
  public DataResponse query(ResourceRequest queryParam) {
    ExternalResourceRequest externalQueryParam = (ExternalResourceRequest) queryParam;
    List<String> orphaCode =
        externalQueryParam.getDiagnosisAvailable().stream()
            .filter(da -> da.contains("ORPHA:"))
            .map(da -> da.replace("ORPHA:", ""))
            .collect(Collectors.toList());

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(serviceBaseURL).queryParam("orphaCode", orphaCode);

    if (queryParam.getResourceType() != null) {
      builder.queryParam("resourceType", queryParam.getResourceType());
    }
    if (queryParam.getCountry() != null) {
      builder.queryParam("country", queryParam.getCountry());
    }
    if (queryParam.getName() != null) {
      builder.queryParam("name", queryParam.getName());
    }
    if (queryParam.getSkip() != null) {
      builder.queryParam("skip", queryParam.getSkip());
    }
    if (queryParam.getLimit() != null) {
      builder.queryParam("limit", queryParam.getLimit());
    }

    ResponseEntity<String> response;
    try {
      response = restTemplate.getForEntity(builder.buildAndExpand().encode().toUri(), String.class);
    } catch (ResourceAccessException | HttpClientErrorException ex) {
      throw new ExternalSourceErrorException();
    }

    // TODO: handle NullPointerException in case response.getBody() is malformed
    LOG.info("Resource returned code {}", response.getStatusCode());
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return DataResponse.fromJson(response.getBody());
    } else {
      throw new ExternalSourceErrorException();
    }
  }

  //  @Override
  //  public DataResponse getById(String resourceId) {
  //    return null;
  //  }
}
