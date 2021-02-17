package org.molgenis.api.ejprd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class ExternalSourceQueryServiceTest {

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
                "http://mock.it/resource/search?orphaCode=63&resourceType=BiobankDataset,PatientRegistryDataset",
                String.class))
        .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));
    ExternalSourceQueryService service = new ExternalSourceQueryService();
    service.setServiceBaseURL("http://mock.it/resource/search");
    service.setRestTemplate(restTemplate);
    List<String> resourceType = new ArrayList<>();
    resourceType.add("BiobankDataset");
    resourceType.add("PatientRegistryDataset");
    DataResponse response = service.query("63", resourceType, null, null, null);
    assertEquals(response.getApiVersion(), "v2");
  }

  @Test
  public void testExternalQueryServiceMissingMandatoryParam() {

    String errorResponseJson =
        "{" + "\"code\": 400 ," + "\"message\": \"Bad Request: missing orphaCode parameter\"" + "}";

    Mockito.when(
            restTemplate.getForEntity("http://mock.it/resource/search?orphaCode=", String.class))
        .thenReturn(new ResponseEntity<>(errorResponseJson, HttpStatus.BAD_REQUEST));

    ExternalSourceQueryService service = new ExternalSourceQueryService();
    service.setServiceBaseURL("http://mock.it/resource/search");
    service.setRestTemplate(restTemplate);
    ErrorResponse errorResponse = service.query("", null, null, null, null);
    assertEquals(errorResponse.getCode(), 400);
    assertEquals(errorResponse.getMessage(), "Bad Request: missing orphaCode parameter");
  }

  @Test
  public void testExternalQueryService404NotFound() {
    String errorResponseJson =
        "{" + "\"code\": 404 ," + "\"message\": \"The requested resource cannot be found\"" + "}";

    Mockito.when(restTemplate.getForEntity("http://mock.it/search?orphaCode=63", String.class))
        .thenReturn(new ResponseEntity<>(errorResponseJson, HttpStatus.NOT_FOUND));

    ExternalSourceQueryService service = new ExternalSourceQueryService();
    service.setServiceBaseURL("http://mock.it/search");
    service.setRestTemplate(restTemplate);
    ErrorResponse errorResponse = service.query("63", null, null, null, null);
    assertEquals(errorResponse.getCode(), 404);
    assertEquals(errorResponse.getMessage(), "The requested resource cannot be found");
  }
}
