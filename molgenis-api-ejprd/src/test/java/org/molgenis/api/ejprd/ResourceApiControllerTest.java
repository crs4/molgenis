package org.molgenis.api.ejprd;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.test.AbstractMockitoSpringContextTests;
import org.molgenis.web.converter.GsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebAppConfiguration
@ContextConfiguration(classes = {GsonConfig.class})
class ResourceApiControllerTest extends AbstractMockitoSpringContextTests {

  private DataService dataService;

  private MockMvc mockMvc;

  @Autowired private GsonHttpMessageConverter gsonHttpMessageConverter;

  @BeforeEach
  void beforeTest() {
    dataService = mock(DataService.class);
    ResourceApiController controller = new ResourceApiController(dataService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(gsonHttpMessageConverter)
            .build();
  }



  @Test
  void getAllResourcesTest() throws Exception {
    reset(dataService);

    Entity biobank1 = mock(Entity.class);
    when(biobank1.getString(eq("name"))).thenReturn("Biobank_1");

    Entity collection1 = mock(Entity.class);
    when(collection1.getString(eq("name"))).thenReturn("Collection_1");
    when(collection1.get(eq("biobank"))).thenReturn(biobank1);
    when(collection1.getString(eq("id"))).thenReturn("12345");
    when(collection1.getString(eq("description"))).thenReturn("This is biobank 1");

    Entity biobank2 = mock(Entity.class);
    when(biobank2.getString(eq("name"))).thenReturn("Biobank_2");

    Entity collection2 = mock(Entity.class);
    when(collection2.getString(eq("name"))).thenReturn("Collection_2");
    when(collection2.get(eq("biobank"))).thenReturn(biobank2);
    when(collection2.getString(eq("id"))).thenReturn("6789");
    when(collection2.getString(eq("description"))).thenReturn("This is biobank 2");

    List<Entity> entities = new ArrayList<>();
    entities.add(collection1);
    entities.add(collection2);

    when(dataService.findAll(eq("eu_bbmri_eric_collections"), any(Query.class)))
        .thenReturn(entities.stream());

    this.mockMvc
        .perform(get(URI.create("http://molgenis01.gcc.rug.nl:8080/api/ejprd/resource")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apiVersion", is("v1")))
        .andExpect(jsonPath("$.resourceResponses", hasSize(2)))
        .andExpect(jsonPath("$.resourceResponses[0].name", is("Biobank_1 - Collection_1")))
        .andExpect(jsonPath("$.resourceResponses[0].id", is("12345")))
        .andExpect(jsonPath("$.resourceResponses[0].description", is("This is biobank 1")))
        .andExpect(jsonPath("$.resourceResponses[0].url", is("http://molgenis01.gcc.rug.nl:8080/menu/main/app-molgenis-app-biobank-explorer/collection/12345")))
        .andExpect(jsonPath("$.resourceResponses[1].name", is("Biobank_2 - Collection_2")))
        .andExpect(jsonPath("$.resourceResponses[1].id", is("6789")))
        .andExpect(jsonPath("$.resourceResponses[1].description", is("This is biobank 2")))
        .andExpect(jsonPath("$.resourceResponses[1].url", is("http://molgenis01.gcc.rug.nl:8080/menu/main/app-molgenis-app-biobank-explorer/collection/6789")))
        .andExpect(content().contentTypeCompatibleWith("application/json"));
  }

  @Test
  void getOneResourcesTest() throws Exception {
    reset(dataService);

    Entity biobank = mock(Entity.class);
    when(biobank.getString(eq("name"))).thenReturn("Biobank_1");

    Entity collection = mock(Entity.class);
    when(collection.getString(eq("name"))).thenReturn("Collection_1");
    when(collection.get(eq("biobank"))).thenReturn(biobank);
    when(collection.getString(eq("id"))).thenReturn("12345");
    when(collection.getString(eq("description"))).thenReturn("This is biobank 1");

    Query<Entity> q = new QueryImpl<>();
    q.nest();
    q.eq("diagnosis_available.code", "ORPHA:145");
    q.and();
    q.eq("diagnosis_available.ontology", "orphanet");
    q.unnest();
    when(dataService.findAll("eu_bbmri_eric_collections", q))
        .thenReturn(Stream.of(collection));

    this.mockMvc
        .perform(get(URI.create("http://molgenis01.gcc.rug.nl:8080/api/ejprd/resource?orphaCode=145")))
        .andExpect(jsonPath("$.apiVersion", is("v1")))
        .andExpect(jsonPath("$.resourceResponses", hasSize(1)))
        .andExpect(jsonPath("$.resourceResponses[0].name", is("Biobank_1 - Collection_1")))
        .andExpect(jsonPath("$.resourceResponses[0].id", is("12345")))
        .andExpect(jsonPath("$.resourceResponses[0].description", is("This is biobank 1")))
        .andExpect(jsonPath("$.resourceResponses[0].url", is("http://molgenis01.gcc.rug.nl:8080/menu/main/app-molgenis-app-biobank-explorer/collection/12345")))
        .andExpect(status().isOk());
  }
}
