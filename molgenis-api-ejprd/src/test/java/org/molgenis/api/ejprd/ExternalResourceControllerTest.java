package org.molgenis.api.ejprd;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.service.ExternalSourceQueryService;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.test.AbstractMockitoSpringContextTests;
import org.molgenis.web.converter.GsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

@WebAppConfiguration
@ContextConfiguration(classes = {GsonConfig.class})
class ExternalResourceControllerTest extends AbstractMockitoSpringContextTests {

  private MockMvc mockMvc;
  private static final String NAME_COLUMN = "name";
  private static final String BASE_URI_COLUMN = "base_uri";
  private static final String SERVICE_URI_COLUMN = "service_uri";

  private static final String BASE_URL = "http://molgenis01.gcc.rug.nl:8080";
  private static final String BASE_API_URL =
      String.format("%s/api/ejprd/external_resource", BASE_URL);
  private static final String EXTERNAL_SOURCE_ID = "external_source";
  private static final String EXTERNAL_SOURCE_NAME = "External Source";
  private static final String EXTERNAL_SOURCE_API_URL = "http://external-source.example.org/api";
  private static final String EXTERNAL_SOURCE_SERVICE_URL =
      "http://external-source.example.org/api";

  private DataService dataService;
  @Autowired private GsonHttpMessageConverter gsonHttpMessageConverter;

  private RestTemplate restTemplate = new RestTemplate();
  private ObjectMapper mapper = new ObjectMapper();

  private MockRestServiceServer mockServer;

  private ExternalSourceQueryService service;
  private HashMap<String, String> testSource;
  private CatalogResponse testCatalogResponse;

  private String expectedJSONResponse =
      "{"
          + "\"apiVersion\": \"v1\","
          + "\"resourceResponses\": [{"
          + "    \"name\" : \"Test biobank project\","
          + "    \"url\" : \"http://biobank.test.it\","
          + "    \"id\" : \"TEST-001\","
          + "    \"description\": \"Test\""
          + "}],"
          + "\"page\": {"
          + "    \"size\": 100,"
          + "    \"totalElements\": 1,"
          + "    \"totalPages\": 1,"
          + "    \"number\": 0"
          + "}"
          + "}";

  @BeforeEach
  public void beforeTest() {
    Entity externalSource = mock(Entity.class);
    when(externalSource.getString(NAME_COLUMN)).thenReturn(EXTERNAL_SOURCE_NAME);
    when(externalSource.getString(BASE_URI_COLUMN)).thenReturn(EXTERNAL_SOURCE_API_URL);
    when(externalSource.getString(SERVICE_URI_COLUMN)).thenReturn(EXTERNAL_SOURCE_SERVICE_URL);

    dataService = mock(DataService.class);
    when(dataService.findOneById(any(), eq(EXTERNAL_SOURCE_ID))).thenReturn(externalSource);

    ExternalResourcesController controller = new ExternalResourcesController(dataService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(gsonHttpMessageConverter)
            .build();

    mockServer = MockRestServiceServer.createServer(restTemplate);
    service = new ExternalSourceQueryService(EXTERNAL_SOURCE_API_URL);
  }

  @Test
  public void testGetExternalResourcesResponseOK() throws Exception {
    mockServer
        .expect(
            ExpectedCount.once(),
            requestTo(
                new URI(
                    String.format("%s/resource/search?orphaCode=123", EXTERNAL_SOURCE_API_URL))))
        .andExpect(method(HttpMethod.GET))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(expectedJSONResponse));
  }
}
