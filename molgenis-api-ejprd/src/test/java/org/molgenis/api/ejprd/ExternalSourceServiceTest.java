package org.molgenis.api.ejprd;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.molgenis.api.ejprd.externalServices.ExternalSourceService;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.web.converter.GsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonObject;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ExternalSourceServiceTest.class)
public class ExternalSourceServiceTest {


  private RestTemplate restTemplate = new RestTemplate();
  private ObjectMapper mapper = new ObjectMapper();

  private MockRestServiceServer mockServer;
  private DataService dataService = mock(DataService.class);
  //private ExternalSourceService service;
  private HashMap<String, String> testSource;
  private CatalogResponse testCatalogResponse;

  @InjectMocks
  private ExternalSourceService service = new ExternalSourceService(dataService);

  private String expectedJSONResponse = "{" +
      "\"apiVersion\": \"v1\","  +
      "\"resourceResponses\": ["+
  "{   \"name\" : \"Test biobank project\","+
      "\"url\" : \"http://biobank.test.it\","+
      "\"id\" : \"TEST-001\"," +
      "\"description\": \"Test\""+
  "}" +
    "]," +
        "\"page\": {"+
    "\"size\": 100,"+
        "\"totalElements\": 1," +
        "\"totalPages\": 1," +
        "\"number\": 0" +
  "}" +
"}";

  private List<Entity> getMockData() {
    List<Entity> externalSources = new ArrayList<>();
    Entity source = mock(Entity.class);
      when(source.get(eq("id")))
          .thenReturn("fooId");
    when(source.get(eq("name")))
        .thenReturn("fooName");
    when(source.get(eq("description")))
        .thenReturn("fooDescription");
    when(source.get(eq("base_uri")))
        .thenReturn("fooBaseUri");
    when(source.get(eq("service_uri")))
        .thenReturn("fooServiceUri");
    externalSources.add(source);
    return externalSources;
  }

  @Before
  public void init() {
    mockServer = MockRestServiceServer.createServer(restTemplate);
    //dataService = mock(DataService.class);
    //service = new ExternalSourceService(dataService);
    testSource = new HashMap();
    testSource.put("id", "fooId");
    testSource.put("name", "fooName");
    testSource.put("description", "fooDescription");
    testSource.put("base_uri", "fooBaseUri");
    testSource.put("service_uri", "fooServiceUri");

  }

  @Test
  public void testGetExternalResourcesResponseOK()
      throws Exception {
    mockServer.expect(ExpectedCount.once(),
        requestTo(new URI("http://localhost:8080/api/resource/search?orphaCode=123")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(expectedJSONResponse)
        );
    service.setRestTemplate(restTemplate);
    JsonObject results = service.getExternalResources("http://localhost:8080/api/resource/search", "123");
    CatalogResponse cResponse = service.createExternalServiceCatalogReponse(testSource, results);
    List<Entity> entities = getMockData();
    System.out.println(entities.stream().count());
    when(dataService.findAll("eu_bbmri_eric_external_sources")).thenReturn(entities.stream());
    HashMap<String, HashMap<String, String>>  sources = service.getConfiguredExternalSources();
    CatalogResponse catalogResponse = service.createExternalServiceCatalogReponse(sources.get("fooId"), results);
    Assert.assertEquals(catalogResponse.getResources().get(0).getName(), "Test biobank project");
  }
}
