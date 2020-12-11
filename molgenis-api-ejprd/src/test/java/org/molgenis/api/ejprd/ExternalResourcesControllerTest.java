package org.molgenis.api.ejprd;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.api.ejprd.ExternalResourcesControllerTest.Config;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ErrorResponse;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.api.ejprd.service.ExternalSourceQueryService;
import org.molgenis.api.model.response.PageResponse;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.test.AbstractMockitoSpringContextTests;
import org.molgenis.web.converter.GsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebAppConfiguration
@ContextConfiguration(classes = {Config.class, GsonConfig.class})
public class ExternalResourcesControllerTest extends AbstractMockitoSpringContextTests {

  String BASE_URL = "http://molgenis01.gcc.rug.nl:8080";
  String BASE_API_URL = String.format("%s/api/ejprd/external_sources/", BASE_URL);
  @Autowired private ExternalResourcesController controller;
  @Autowired private DataService dataService;
  @Autowired private Entity source;
  @Autowired private ExternalSourceQueryService queryService;
  @Autowired private GsonHttpMessageConverter gsonHttpMessageConverter;
  private MockMvc mockMvc;

  // @Before
  // public void setup() {

  // this.mockMvc = standaloneSetup(new ExternalResourcesController(dataService)).build();
  // this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  // }

  @BeforeEach
  void beforeMethod() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(gsonHttpMessageConverter)
            .build();
  }

  @Test
  void testRequestOK() throws Exception {

    controller = mock(ExternalResourcesController.class);

    ResultActions resultActions =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(
                    String.format(
                        "%sEXT_1?diagnosisAvailable=ORPHA:63&skip=1&limit=5", BASE_API_URL)))
            .andExpect(MockMvcResultMatchers.status().isOk());

    MvcResult result = resultActions.andReturn();
    String content = result.getResponse().getContentAsString();
    Gson gson = new Gson();
    JsonObject jsonDataResponse = gson.fromJson(content, JsonObject.class);
    JsonArray catalogs = jsonDataResponse.getAsJsonArray("catalogs");
    assertEquals(catalogs.size(), 1);
    JsonObject catalog = (JsonObject) catalogs.get(0);
    assertEquals("External source 1", catalog.get("name").getAsString());
    JsonArray catalogResources = catalog.getAsJsonArray("resources");
    assertEquals(catalogResources.size(), 1);
    JsonObject resource = (JsonObject) catalogResources.get(0);
    assertEquals("biobank:1", resource.get("id").getAsString());
    assertEquals("Biobank", resource.get("type").getAsString());
    assertEquals("Biobank 1", resource.get("name").getAsString());
    assertEquals("This is Biobank 1", resource.get("description").getAsString());
    assertEquals("https://biobank.url/", resource.get("homepage").getAsString());
  }

  @Test
  void testRequestUnknownExternalSource() throws Exception {
    controller = mock(ExternalResourcesController.class);
    ResultActions resultActions =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(
                    String.format(
                        "%sUNKNOWN?diagnosisAvailable=ORPHA:63&skip=1&limit=5", BASE_API_URL)))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError());

    MvcResult result = resultActions.andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(response.getStatus(), 404);
  }

  @Test
  void testRequestMandatoryParamNotProvided() throws Exception {
    controller = mock(ExternalResourcesController.class);
    ResultActions resultActions =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(String.format("%sEXT_1?skip=1&limit=5", BASE_API_URL)))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  void testRequestMandatoryParameterProvidedNull() throws Exception {
    controller = mock(ExternalResourcesController.class);
    ResultActions resultActions =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(
                    String.format("%sEXT_1?diagnosisAvailable=&skip=1&limit=5", BASE_API_URL)))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Configuration
  static class Config {

    @Bean
    DataService dataService() {
      DataService dataService = mock(DataService.class);
      Entity externalSource = mock(Entity.class);
      lenient().when(externalSource.getString(eq("id"))).thenReturn("EXT_1");
      lenient().when(externalSource.getString(eq("name"))).thenReturn("External source 1");
      lenient()
          .when(externalSource.getString(eq("base_uri")))
          .thenReturn("http://ext1.it/resource");
      lenient()
          .when(externalSource.getString(eq("service_uri")))
          .thenReturn("http://ext1.it/resource");

      lenient()
          .when(dataService.findOneById("eu_bbmri_eric_external_sources", "EXT_1"))
          .thenReturn(externalSource);
      return dataService;
    }

    @Bean
    ExternalSourceQueryService queryService() {
      ExternalSourceQueryService queryService = mock(ExternalSourceQueryService.class);
      String apiVersion = "v1";
      ResourceResponse resourceResponse =
          ResourceResponse.create(
              "biobank:1",
              "Biobank",
              "Biobank 1",
              "This is Biobank 1",
              "https://biobank.url/",
              null);
      List<ResourceResponse> resourceResponses = Collections.singletonList(resourceResponse);
      PageResponse pageResponse = PageResponse.create(2, 10, 1);
      ErrorResponse errorResponse = ErrorResponse.create(1, "Error");
      DataResponse dataResponse =
          DataResponse.create(apiVersion, resourceResponses, pageResponse, errorResponse);

      lenient().when(queryService.query("63", null, 1, 5)).thenReturn(dataResponse);
      return queryService;
    }

    @Bean
    ExternalResourcesController controller() {
      return new ExternalResourcesController(dataService(), queryService());
    }

    @Bean
    Entity source() {
      Entity source = mock(Entity.class);
      lenient().when(source.getString(eq("id"))).thenReturn("EXT_1");
      lenient().when(source.getString(eq("name"))).thenReturn("External source 1");
      lenient().when(source.getString(eq("base_uri"))).thenReturn("http://ext1.it/resource");
      lenient().when(source.getString(eq("service_uri"))).thenReturn("http://ext1.it/resource");
      return source;
    }
  }
}
