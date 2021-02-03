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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
  private DataService biobanksLookupDataService;

  private MockMvc mockMvc;

  @Autowired private GsonHttpMessageConverter gsonHttpMessageConverter;

  @BeforeEach
  void beforeTest() {
    dataService = mock(DataService.class);
    biobanksLookupDataService = mock(DataService.class);
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

  private HashMap<String,List<Entity>> getMockResourcesSplittedData(int size) {
    HashMap entities = new HashMap();
    Entity country = mock(Entity.class);
    lenient().when(country.getString(eq("id"))).thenReturn("IT");
    lenient().when(country.getString(eq("name"))).thenReturn("Italy");

    //lenient().when(ressourceType.getString(eq("id"))).thenReturn(BBMRI_BIOBANK_TYPE);

    List<Entity> collections_bb_type = new ArrayList<>();
    List<Entity> collections_reg_type = new ArrayList<>();
    List<Entity> biobanks_bb_type = new ArrayList<>();
    List<Entity> biobanks_reg_type = new ArrayList<>();
    boolean is_registry = false;

    for (int i = 0; i < size; i++) {
      is_registry = false;
      Entity ressourceType = mock(Entity.class);
      Entity biobank = mock(Entity.class);
      lenient()
          .when(biobank.getString(eq("id")))
          .thenReturn(String.format("Biobank_%s", i+1));

      if (Arrays.asList(0,1,3,5,6,7).contains(i)){
        lenient().when(ressourceType.getString(eq("id"))).thenReturn(BBMRI_BIOBANK_TYPE);
      }
      else{
        is_registry = true;
        lenient().when(ressourceType.getString(eq("id"))).thenReturn(BBMRI_REGISTRY_TYPE);
      }
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
      if (is_registry) {
        biobanks_reg_type.add(biobank);
        collections_reg_type.add(collection);
      }
      else{
        biobanks_bb_type.add(biobank);
        collections_bb_type.add(collection);
      }

    }

    entities.put("biobanks_bb_type", biobanks_bb_type);
    entities.put("biobanks_reg_type", biobanks_reg_type);
    entities.put("collections_bb_type", collections_bb_type);
    entities.put("collections_reg_type", collections_reg_type);

    return entities;
  }

  private Query<Entity> getQuery(
      boolean includeCode,
      boolean includeName,
      String resourceType,
      @Nullable Integer pageSize,
      @Nullable Integer offset) {
    Query<Entity> q = new QueryImpl<>();
    if (includeCode) {
      q.nest();
      q.eq("diagnosis_available.code", String.format("ORPHA:%s", ORPHA_CODE));
      q.and();
      q.eq("diagnosis_available.ontology", "orphanet");
      if(resourceType == BBMRI_BIOBANK_TYPE){
        q.and();
        q.in("biobank", Arrays.asList("Biobank_1", "Biobank_2","Biobank_4","Biobank_6","Biobank_7","Biobank_8"));
      }
      else if (resourceType == BBMRI_REGISTRY_TYPE){
        q.and();
        q.in("biobank", Arrays.asList("Biobank_3", "Biobank_5","Biobank_9","Biobank_10"));
      }
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

  private Query<Entity> getLookupResourceTypeQuery(String resourceTypeValue){
    Query<Entity> q = new QueryImpl<>();
    q.nest();
    q.eq("ressource_types", resourceTypeValue);
    q.unnest();
    return q;
  }

  private Query<Entity> getEmptyQuery() {
    return getQuery(false, false, null, null, null);
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
    Query<Entity> findalAllQuery = getQuery(true, false, null, 5, null);
    Query<Entity> countQuery = getQuery(true, false, null, null, null);
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
  void testGetResourceWithPagingMinLimit() throws Exception {
    this.mockMvc
        .perform(
            get(URI.create(String.format("%s?orphaCode=%s&limit=0", BASE_API_URL, ORPHA_CODE))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetResourceWithPagingMinSkip() throws Exception {
    this.mockMvc
        .perform(
            get(URI.create(String.format("%s?orphaCode=%s&skip=-1", BASE_API_URL, ORPHA_CODE))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetResourceWithPagingMaxOffset() throws Exception {
    this.mockMvc
        .perform(
            get(
                URI.create(
                    String.format("%s?orphaCode=%s&skip=100&limit=100", BASE_API_URL, ORPHA_CODE))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetResourcesWithPagingSecondPage() throws Exception {
    reset(dataService);
    int resultSize = 5;
    Query<Entity> findAllQuery = getQuery(true, false, null, 5, 5);
    Query<Entity> countQuery = getQuery(true, false, null, null, null);
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

    Query<Entity> findAllQuery = getQuery(true, false, null,  100, null);
    Query<Entity> countQuery = getQuery(true, false, null, null, null);
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

    Query<Entity> findAllQuery = getQuery(true, true, null, 100, null);
    Query<Entity> countQuery = getQuery(true, true, null,  null, null);

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

  @Test
  void testGetResourceByCodeAndResourceTypeBiobank() throws Exception {
    reset(dataService);
    int resultSize = 10;
    HashMap<String, List<Entity> > entities = getMockResourcesSplittedData(resultSize);
    List<Entity> collectionEntities = entities.get("collections_bb_type");
    List<Entity> biobankEntities = entities.get("biobanks_bb_type");

    Query<Entity> findAllQuery = getQuery(true, true, BBMRI_BIOBANK_TYPE,  100, null);
    Query<Entity> countQuery = getQuery(true, true, BBMRI_BIOBANK_TYPE,  null, null);
    Query<Entity> biobankTypeQuery = getLookupResourceTypeQuery("BIOBANK");

    lenient().when(dataService.count(ENTITY_ID, countQuery)).thenReturn((long) resultSize);
    lenient().when(dataService.findAll(ENTITY_ID, findAllQuery)).thenReturn(collectionEntities.stream());
    lenient().when(dataService.findAll("eu_bbmri_eric_biobanks", biobankTypeQuery)).thenReturn(biobankEntities.stream());
    ResultActions resultActions =
        this.mockMvc.perform(
            get(
                URI.create(
                    String.format(
                        "%s?name=%s&orphaCode=%s&type=%s", BASE_API_URL, DISEASE_NAME, ORPHA_CODE,"BiobankDataset"))));
    resultActions.andExpect(status().isOk());
    resultActions
        .andExpect(jsonPath("$.resourceResponses", hasSize(6)));

  }
  @Test
  void testGetResourceByCodeAndResourceTypeRegistry() throws Exception {
    reset(dataService);
    int resultSize = 10;
    HashMap<String, List<Entity> > entities = getMockResourcesSplittedData(resultSize);
    List<Entity> collectionEntities = entities.get("collections_reg_type");
    List<Entity> biobankEntities = entities.get("biobanks_reg_type");

    Query<Entity> findAllQuery = getQuery(true, true, BBMRI_REGISTRY_TYPE,  100, null);
    Query<Entity> countQuery = getQuery(true, true, BBMRI_REGISTRY_TYPE,  null, null);
    Query<Entity> biobankTypeQuery = getLookupResourceTypeQuery("REGISTRY");

    lenient().when(dataService.count(ENTITY_ID, countQuery)).thenReturn((long) resultSize);
    lenient().when(dataService.findAll(ENTITY_ID, findAllQuery)).thenReturn(collectionEntities.stream());
    lenient().when(dataService.findAll("eu_bbmri_eric_biobanks", biobankTypeQuery)).thenReturn(biobankEntities.stream());
    ResultActions resultActions =
        this.mockMvc.perform(
            get(
                URI.create(
                    String.format(
                        "%s?name=%s&orphaCode=%s&type=%s", BASE_API_URL, DISEASE_NAME, ORPHA_CODE,"PatientRegistryDataset"))));
    resultActions.andExpect(status().isOk());
    resultActions
        .andExpect(jsonPath("$.resourceResponses", hasSize(4)));




  }
}