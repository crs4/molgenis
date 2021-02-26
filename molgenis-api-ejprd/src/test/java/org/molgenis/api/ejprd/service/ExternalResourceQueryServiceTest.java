package org.molgenis.api.ejprd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.molgenis.api.ejprd.exceptions.ExternalSourceErrorException;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ExternalResourceRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class ExternalResourceQueryServiceTest {

  @Mock private RestTemplate restTemplate;

  @Test
  public void testExternalQueryServiceResultsOK() {

    // Expected Response
    String expectedResponse =
        "{"
            + "\"apiVersion\": \"v2\","
            + "\"resourceResponses\": [ "
            + "{"
            + "\"id\": \"biobank:1\","
            + "\"name\": \"BB1 Name\","
            + "\"type\": \"BB1 Type\","
            + "\"description\": \"BB1 Description\","
            + "\"homepage\": \"http://bb1.it\","
            + "\"publisher\": {"
            + "\"id\": \"BB1 Publisher Id\","
            + "\"name\": \"BB1 Publisher Name\","
            + "\"location\": {"
            + "\"id\": \"IT\","
            + "\"country\": \"Italy\""
            + "}"
            + "}"
            + "}"
            + "],"
            + "\"page\": {"
            + "\"size\": 100,"
            + "\"totalElements\": 1,"
            + "\"totalPages\": 1,"
            + "\"number\": 0"
            + "}"
            + "}";

    Mockito.when(
            restTemplate.getForEntity(
                "http://mock.it/resource/search"
                    + "?orphaCode=63"
                    + "&resourceType=BiobankDataset,PatientRegistryDataset"
                    + "&country=IT"
                    + "&skip=0&limit=100",
                String.class))
        .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));
    ExternalResourceQueryService service = new ExternalResourceQueryService();
    service.setServiceBaseURL("http://mock.it/resource/search");
    service.setRestTemplate(restTemplate);

    List<String> resourceType = new ArrayList<>();
    resourceType.add("BiobankDataset");
    resourceType.add("PatientRegistryDataset");

    ExternalResourceRequest request = new ExternalResourceRequest();
    request.setDiagnosisAvailable(Collections.singletonList("ORPHA:63"));
    request.setCountry(Collections.singletonList("IT"));
    request.setResourceType(resourceType);

    DataResponse response = service.query(request);
    assertEquals(response.getApiVersion(), "v2");
  }

  @Test
  public void testExternalQueryServiceMissingMandatoryParam() {
    Mockito.when(
            restTemplate.getForEntity(
                "http://mock.it/resource/search?orphaCode=&skip=0&limit=100", String.class))
        .thenThrow(HttpClientErrorException.class);

    ExternalResourceQueryService service = new ExternalResourceQueryService();
    service.setServiceBaseURL("http://mock.it/resource/search");
    service.setRestTemplate(restTemplate);

    ExternalResourceRequest request = new ExternalResourceRequest();
    request.setDiagnosisAvailable(Collections.singletonList(""));

    assertThrows(ExternalSourceErrorException.class, () -> service.query(request));
  }

  @Test
  public void testExternalQueryService404NotFound() {
    String errorResponseJson =
        "{" + "\"code\": 404 ," + "\"message\": \"The requested resource cannot be found\"" + "}";

    Mockito.when(
            restTemplate.getForEntity(
                "http://mock.it/search?orphaCode=63&skip=0&limit=100", String.class))
        .thenReturn(new ResponseEntity<>(errorResponseJson, HttpStatus.NOT_FOUND));

    ExternalResourceQueryService service = new ExternalResourceQueryService();
    service.setServiceBaseURL("http://mock.it/search");
    service.setRestTemplate(restTemplate);

    ExternalResourceRequest request = new ExternalResourceRequest();
    request.setDiagnosisAvailable(Collections.singletonList("ORPHA:63"));

    assertThrows(ExternalSourceErrorException.class, () -> service.query(request));
  }
}
