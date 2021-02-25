package org.molgenis.api.ejprd;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.api.ejprd.ExternalSourcesControllerTest.Config;
import org.molgenis.api.ejprd.controller.ExternalSourcesController;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ErrorResponse;
import org.molgenis.api.ejprd.model.ExternalResourceRequest;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.api.ejprd.service.ExternalResourceQueryService;
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
public class ExternalSourcesControllerTest extends AbstractMockitoSpringContextTests {

  private static final Logger LOG = LoggerFactory.getLogger(ExternalSourcesControllerTest.class);
  private static final String BASE_URL = "http://molgenis01.gcc.rug.nl:8080";
  private static final String BASE_API_URL =
      String.format("%s/api/ejprd/external_sources/", BASE_URL);

  @Autowired private DataService dataService;
  @Autowired private Entity source;
  @Autowired private ExternalResourceQueryService queryService;
  @Autowired private GsonHttpMessageConverter gsonHttpMessageConverter;

  private MockMvc mockMvc;

  @BeforeEach
  void beforeMethod() {
    ExternalSourcesController controller = new ExternalSourcesController(dataService, queryService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(gsonHttpMessageConverter)
            .build();
  }

  @Test
  void testRequestOK() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                String.format(
                    "%sEXT_1?diagnosisAvailable=ORPHA:63&"
                        + "resourceType=BiobankDataset,PatientRegistryDataset"
                        + "&country=IT"
                        + "&skip=0&limit=5",
                    BASE_API_URL)))
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
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                String.format(
                    "%sUNKNOWN?diagnosisAvailable=ORPHA:63&skip=0&limit=5", BASE_API_URL)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testRequestWrongResourceTypeValue() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                String.format(
                    "%sEXT_1?diagnosisAvailable=ORPHA:63&resourceType=UNK&skip=0&limit=5",
                    BASE_API_URL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testRequestMandatoryParamNotProvided() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(String.format("%sEXT_1?skip=0&limit=5", BASE_API_URL)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void testRequestMandatoryParameterProvidedNull() throws Exception {
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
    ExternalResourceQueryService queryService() {
      ExternalResourceQueryService queryService = mock(ExternalResourceQueryService.class);
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

      List<String> resourceType = new ArrayList<>();
      resourceType.add("BiobankDataset");
      resourceType.add("PatientRegistryDataset");

      ExternalResourceRequest request = new ExternalResourceRequest();
      request.setDiagnosisAvailable(Collections.singletonList("ORPHA:63"));
      request.setResourceType(resourceType);
      request.setCountry(Collections.singletonList("IT"));
      request.setLimit(5);
      request.setSkip(0);

      lenient().when(queryService.query(request)).thenReturn(dataResponse);

      return queryService;
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
