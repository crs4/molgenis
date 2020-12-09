package org.molgenis.api.ejprd;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
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
import javax.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.api.ejprd.service.InternalResourceQueryService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebAppConfiguration
@ContextConfiguration(classes = {GsonConfig.class})
class ResourceApiControllerTest extends AbstractMockitoSpringContextTests {

  private static final String EJPRD_BIOBANK_TYPE = "BiobankDataset";
  private static final String EJPRD_REGISTRY_TYPE = "PatientRegistryDataset";
  private static final String BBMRI_BIOBANK_TYPE = "BIOBANK";
  private static final String BBMRI_REGISTRY_TYPE = "REGISTRY";
  private static final String BIOBANK_BASE_NAME = "Biobank_";
  private static final String ORGANIZER_BASE_NAME = "Organizer_";
  private static final String COLLECTION_BASE_NAME = "Collection_";
  private static final String COLLECTION_DESCRIPTION = "This is biobank ";
  private static final String BASE_URL = "http://test.url";
  private static final String BASE_API_URL =
      String.format("%s/api/ejprd/resource/search", BASE_URL);
  private static final String COLLECTION_URL =
      String.format("%s/menu/main/app-molgenis-app-biobank-explorer/collection", BASE_URL);
  private static final String ENTITY_ID = "eu_bbmri_eric_collections";
  private static final String ORPHA_CODE = "145";
  private static final String DISEASE_NAME = "COVID";

  private DataService dataService;

  private MockMvc mockMvc;

  @Autowired private GsonHttpMessageConverter gsonHttpMessageConverter;

  @BeforeEach
  void beforeTest() {
    dataService = mock(DataService.class);
    InternalResourceQueryService resourceQueryService =
        new InternalResourceQueryService(dataService);
    ResourceApiController controller = new ResourceApiController(resourceQueryService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(gsonHttpMessageConverter)
            .build();
  }

  private List<Entity> getMockData(int size) {
    Entity country = mock(Entity.class);
    lenient().when(country.getString(eq("id"))).thenReturn("IT");
    lenient().when(country.getString(eq("name"))).thenReturn("Italy");

    Entity ressourceType = mock(Entity.class);
    lenient().when(ressourceType.getString(eq("id"))).thenReturn(BBMRI_BIOBANK_TYPE);

    List<Entity> entities = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      Entity biobank = mock(Entity.class);
      lenient()
          .when(biobank.getString(eq("name")))
          .thenReturn(String.format("%s%d", BIOBANK_BASE_NAME, i + 1));
      lenient()
          .when(biobank.getString(eq("juridical_person")))
          .thenReturn(String.format("%s%d", ORGANIZER_BASE_NAME, i + 1));
      lenient().when(biobank.get(eq("ressource_types"))).thenReturn(ressourceType);
      Entity collection = mock(Entity.class);
      lenient()
          .when(collection.getString(eq("name")))
          .thenReturn(String.format("%s%d", COLLECTION_BASE_NAME, i + 1));
      lenient().when(collection.get(eq("biobank"))).thenReturn(biobank);
      lenient().when(collection.getString(eq("id"))).thenReturn(String.valueOf(i + 1));
      lenient()
          .when(collection.getString(eq("description")))
          .thenReturn(String.format("%s%d", COLLECTION_DESCRIPTION, i + 1));
      lenient().when(collection.get(eq("country"))).thenReturn(country);

      entities.add(collection);
    }
    return entities;
  }

  private Query<Entity> getQuery(
      boolean includeCode,
      boolean includeName,
      @Nullable Integer pageSize,
      @Nullable Integer offset) {
    Query<Entity> q = new QueryImpl<>();
    if (includeCode) {
      q.nest();
      q.eq("diagnosis_available.code", String.format("ORPHA:%s", ORPHA_CODE));
      q.and();
      q.eq("diagnosis_available.ontology", "orphanet");
      q.unnest();
    }
    if (includeName) {
      if (includeCode) {
        q.or();
      }
      q.like("diagnosis_available.label", DISEASE_NAME);
    }
    if (pageSize != null) {
      q.pageSize(pageSize);
    }
    if (offset != null) {
      q.offset(offset);
    }
    return q;
  }

  private Query<Entity> getEmptyQuery() {
    return getQuery(false, false, null, null);
  }

  private void checkContentType(ResultActions actions) throws Exception {
    actions.andExpect(content().contentTypeCompatibleWith("application/json"));
  }

  private void checkApiVersion(ResultActions actions) throws Exception {
    actions.andExpect(jsonPath("$.apiVersion", is("v0.2")));
  }

  private void checkPageData(
      ResultActions actions, int size, int totalElements, int totalPages, int number)
      throws Exception {
    actions
        .andExpect(jsonPath("$.page.size", is(size)))
        .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
        .andExpect(jsonPath("$.page.totalPages", is(totalPages)))
        .andExpect(jsonPath("$.page.number", is(number)));
  }

  private void checkResultData(ResultActions actions, int resultsSize) throws Exception {
    for (int i = 0; i < resultsSize; i++) {
      actions
          .andExpect(jsonPath("$.resourceResponses", hasSize(resultsSize)))
          .andExpect(
              jsonPath(
                  String.format("$.resourceResponses[%d].name", i),
                  is(
                      String.format(
                          "%s%d - %s%d", BIOBANK_BASE_NAME, i + 1, COLLECTION_BASE_NAME, i + 1))))
          .andExpect(
              jsonPath(String.format("$.resourceResponses[%s].id", i), is(String.valueOf(i + 1))))
          .andExpect(
              jsonPath(String.format("$.resourceResponses[%s].type", i), is(EJPRD_BIOBANK_TYPE)))
          .andExpect(
              jsonPath(
                  String.format("$.resourceResponses[%s].description", i),
                  is(String.format("%s%d", COLLECTION_DESCRIPTION, i + 1))))
          .andExpect(
              jsonPath(
                  String.format("$.resourceResponses[%s].homepage", i),
                  is(String.format("%s/%d", COLLECTION_URL, i + 1))));
    }
  }

  @Test
  void testGetResourcesWithoutParameters() throws Exception {
    this.mockMvc.perform(get(URI.create(BASE_API_URL))).andExpect(status().isBadRequest());

    this.mockMvc
        .perform(get(URI.create(String.format("%s?orphaCode", BASE_API_URL))))
        .andExpect(status().isBadRequest());

    this.mockMvc
        .perform(get(URI.create(String.format("%s?orphaCode=", BASE_API_URL))))
        .andExpect(status().isBadRequest());

    this.mockMvc
        .perform(get(URI.create(String.format("%s?skip=1&limit=5", BASE_API_URL))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetResourcesWithPaging() throws Exception {
    reset(dataService);
    int resultSize = 5;
    Query<Entity> findalAllQuery = getQuery(true, false, 5, null);
    Query<Entity> countQuery = getQuery(true, false, null, null);
    findalAllQuery.pageSize(5);

    List<Entity> entities = getMockData(5);
    when(dataService.count(ENTITY_ID, countQuery)).thenReturn((long) resultSize * 2);
    when(dataService.findAll(ENTITY_ID, findalAllQuery)).thenReturn(entities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(URI.create(String.format("%s?orphaCode=%s&limit=5", BASE_API_URL, ORPHA_CODE))));
    resultActions.andExpect(status().isOk());

    checkContentType(resultActions);
    checkApiVersion(resultActions);
    checkResultData(resultActions, resultSize);
    checkPageData(resultActions, 5, resultSize * 2, 2, 0);
  }

  @Test
  void testGetResourcesWithPagingSecondPage() throws Exception {
    reset(dataService);
    int resultSize = 5;
    Query<Entity> findAllQuery = getQuery(true, false, 5, 5);
    Query<Entity> countQuery = getQuery(true, false, null, null);
    findAllQuery.pageSize(5);
    findAllQuery.offset(5);

    List<Entity> entities = getMockData(resultSize);
    when(dataService.count(ENTITY_ID, countQuery)).thenReturn((long) resultSize * 2);
    when(dataService.findAll(ENTITY_ID, findAllQuery)).thenReturn(entities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(
                URI.create(
                    String.format("%s?orphaCode=%s&skip=1&limit=5", BASE_API_URL, ORPHA_CODE))));
    resultActions.andExpect(status().isOk());

    checkContentType(resultActions);
    checkApiVersion(resultActions);
    checkResultData(resultActions, resultSize);
    checkPageData(resultActions, 5, resultSize * 2, 2, 1);
  }

  @Test
  void testGetResourcesByCode() throws Exception {
    reset(dataService);
    int resultSize = 1;
    List<Entity> entities = getMockData(1);

    Query<Entity> findAllQuery = getQuery(true, false, 100, null);
    Query<Entity> countQuery = getQuery(true, false, null, null);
    when(dataService.count(ENTITY_ID, countQuery)).thenReturn((long) resultSize);
    when(dataService.findAll(ENTITY_ID, findAllQuery)).thenReturn(entities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(URI.create(String.format("%s?orphaCode=%s", BASE_API_URL, ORPHA_CODE))));
    resultActions.andExpect(status().isOk());

    checkContentType(resultActions);
    checkApiVersion(resultActions);
    checkResultData(resultActions, resultSize);
    checkPageData(resultActions, 100, resultSize, 1, 0);
  }

  //  @Test
  //  void testGetResourcesByName() throws Exception {
  //    reset(dataService);
  //    int resultSize = 10;
  //    List<Entity> entities = getMockData(resultSize);
  //
  //    Query<Entity> findAllQuery = getQuery(false, true, 100, null);
  //    Query<Entity> countQuery = getQuery(false, true, null, null);
  //
  //    when(dataService.count(ENTITY_ID, countQuery)).thenReturn((long) resultSize);
  //    when(dataService.findAll(ENTITY_ID, findAllQuery)).thenReturn(entities.stream());
  //
  //    ResultActions resultActions =
  //        this.mockMvc.perform(
  //            get(URI.create(String.format("%s?name=%s", BASE_API_URL, DISEASE_NAME))));
  //    resultActions.andExpect(status().isOk());
  //
  //    checkContentType(resultActions);
  //    checkApiVersion(resultActions);
  //    checkResultData(resultActions, resultSize);
  //    checkPageData(resultActions, 100, resultSize, 1, 0);
  //  }

  @Test
  void testGetResourcesByCodeAndName() throws Exception {
    reset(dataService);
    int resultSize = 10;
    List<Entity> entities = getMockData(resultSize);

    Query<Entity> findAllQuery = getQuery(true, true, 100, null);
    Query<Entity> countQuery = getQuery(true, true, null, null);

    when(dataService.count(ENTITY_ID, countQuery)).thenReturn((long) resultSize);
    when(dataService.findAll(ENTITY_ID, findAllQuery)).thenReturn(entities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(
                URI.create(
                    String.format(
                        "%s?name=%s&orphaCode=%s", BASE_API_URL, DISEASE_NAME, ORPHA_CODE))));
    resultActions.andExpect(status().isOk());

    checkContentType(resultActions);
    checkApiVersion(resultActions);
    checkResultData(resultActions, resultSize);
    checkPageData(resultActions, 100, resultSize, 1, 0);
  }

  @Test
  void testGetResourcesWithParamsWithoutValues() throws Exception {
    ResultActions resultActions =
        this.mockMvc.perform(get(URI.create(String.format("%s?orphaCode", BASE_API_URL))));
    resultActions.andExpect(status().isBadRequest());
    //      resultActions.andExpect(jsonPath("$.errors", is("")));
  }
}
