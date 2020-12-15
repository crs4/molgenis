package org.molgenis.api.ejprd;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebAppConfiguration
@ContextConfiguration(classes = {Config.class, GsonConfig.class})
public class ExternalResourcesControllerTest extends AbstractMockitoSpringContextTests {

  private static final Logger LOG = LoggerFactory.getLogger(ExternalResourcesControllerTest.class);
  private static final String BASE_URL = "http://molgenis01.gcc.rug.nl:8080";
  private static final String BASE_API_URL =
      String.format("%s/api/ejprd/external_sources/", BASE_URL);

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

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                String.format("%sEXT_1?diagnosisAvailable=ORPHA:63&skip=0&limit=5", BASE_API_URL)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("External source 1")))
        .andExpect(jsonPath("$.url", is("http://ext1.it/resource")))
        .andExpect(jsonPath("$.resources[0].id", is("biobank:1")))
        .andExpect(jsonPath("$.resources[0].type", is("BiobankDataset")))
        .andExpect(jsonPath("$.resources[0].name", is("Biobank 1")))
        .andExpect(jsonPath("$.resources[0].description", is("This is Biobank 1")))
        .andExpect(jsonPath("$.resources[0].homepage", is("https://biobank.url/")))
        .andExpect(jsonPath("$.page.size", is(5)))
        .andExpect(jsonPath("$.page.totalElements", is(1)))
        .andExpect(jsonPath("$.page.totalPages", is(1)))
        .andExpect(jsonPath("$.page.number", is(0)));
  }

  @Test
  void testRequestUnknownExternalSource() throws Exception {
    controller = mock(ExternalResourcesController.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                String.format(
                    "%sUNKNOWN?diagnosisAvailable=ORPHA:63&skip=0&limit=5", BASE_API_URL)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testRequestMandatoryParamNotProvided() throws Exception {
    controller = mock(ExternalResourcesController.class);
    mockMvc
        .perform(MockMvcRequestBuilders.get(String.format("%sEXT_1?skip=0&limit=5", BASE_API_URL)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void testRequestMandatoryParameterProvidedNull() throws Exception {
    controller = mock(ExternalResourcesController.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                String.format("%sEXT_1?diagnosisAvailable=&skip=0&limit=5", BASE_API_URL)))
        .andExpect(status().is4xxClientError());
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
              "BiobankDataset",
              "Biobank 1",
              "This is Biobank 1",
              "https://biobank.url/",
              null);
      List<ResourceResponse> resourceResponses = Collections.singletonList(resourceResponse);
      PageResponse pageResponse = PageResponse.create(5, 1, 0);
      ErrorResponse errorResponse = ErrorResponse.create(1, "Error");
      DataResponse dataResponse =
          DataResponse.create(apiVersion, resourceResponses, pageResponse, errorResponse);

      lenient().when(queryService.query("63", null, 0, 5)).thenReturn(dataResponse);
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
