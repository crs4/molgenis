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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.api.ejprd.controller.impl.ResourceControllerImpl;
import org.molgenis.api.ejprd.service.InternalResourceQueryService;
import org.molgenis.api.ejprd.validators.OrphaCodeOrNameNotNull;
import org.molgenis.api.ejprd.validators.OrphaCodeOrNameNotNullValidator;
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
class ResourceControllerTest extends AbstractMockitoSpringContextTests {

  public static final String DISEASE_TYPES_ENTITY = "eu_bbmri_eric_disease_types";
  public static final String ORPHANET_MATCHING_COLUMN = "orphanet_exact_matching";
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
  private static final String COLLECTIONS_ENTITY_ID = "eu_bbmri_eric_collections";
  private static final String BIOBANK_ENTITY_ID = "eu_bbmri_eric_biobanks";
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
    ResourceControllerImpl controller = new ResourceControllerImpl(resourceQueryService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(gsonHttpMessageConverter)
            .build();
  }

  private List<Entity> getMockData(int size) {
    Entity country = mock(Entity.class);
    lenient().when(country.getString(eq("id"))).thenReturn("IT");
    lenient().when(country.getString(eq("name"))).thenReturn("Italy");

    Entity resourceType = mock(Entity.class);
    lenient().when(resourceType.getString(eq("id"))).thenReturn(BBMRI_BIOBANK_TYPE);

    List<Entity> entities = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      Entity biobank = mock(Entity.class);
      lenient()
          .when(biobank.getString(eq("name")))
          .thenReturn(String.format("%s%d", BIOBANK_BASE_NAME, i + 1));
      lenient()
          .when(biobank.getString(eq("juridical_person")))
          .thenReturn(String.format("%s%d", ORGANIZER_BASE_NAME, i + 1));
      lenient().when(biobank.get(eq("ressource_types"))).thenReturn(resourceType);
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

  private List<Entity> getOrphanetLookupEntities() {
    List<Entity> entities = new ArrayList<>();
    Entity disease = mock(Entity.class);
    lenient().when(disease.getString(eq(ORPHANET_MATCHING_COLUMN))).thenReturn("ORPHA:145");
    lenient().when(disease.getIdValue()).thenReturn("ORPHA:145");
    entities.add(disease);
    return entities;
  }

  private HashMap<String, List<Entity>> getMockResourcesSplittedData(int size) {
    HashMap<String, List<Entity>> entities = new HashMap<>();
    Entity country = mock(Entity.class);
    lenient().when(country.getString(eq("id"))).thenReturn("IT");
    lenient().when(country.getString(eq("name"))).thenReturn("Italy");

    // lenient().when(ressourceType.getString(eq("id"))).thenReturn(BBMRI_BIOBANK_TYPE);

    List<Entity> collectionsBbType = new ArrayList<>();
    List<Entity> collectionsRegType = new ArrayList<>();
    List<Entity> biobanksBbType = new ArrayList<>();
    List<Entity> biobanksRegType = new ArrayList<>();

    boolean isRegistry;
    for (int i = 0; i < size; i++) {
      isRegistry = false;
      Entity resourceType = mock(Entity.class);
      Entity biobank = mock(Entity.class);
      lenient()
          .when(biobank.getIdValue())
          .thenReturn(String.format("%s%d", BIOBANK_BASE_NAME, i + 1));
      lenient()
          .when(biobank.getString("id"))
          .thenReturn(String.format("%s%d", BIOBANK_BASE_NAME, i + 1));

      if (i < 6) {
        lenient().when(resourceType.getString(eq("id"))).thenReturn(BBMRI_BIOBANK_TYPE);
      } else {
        isRegistry = true;
        lenient().when(resourceType.getString(eq("id"))).thenReturn(BBMRI_REGISTRY_TYPE);
      }
      lenient()
          .when(biobank.getString(eq("name")))
          .thenReturn(String.format("%s%d", BIOBANK_BASE_NAME, i + 1));
      lenient()
          .when(biobank.getString(eq("juridical_person")))
          .thenReturn(String.format("%s%d", ORGANIZER_BASE_NAME, i + 1));
      lenient().when(biobank.get(eq("ressource_types"))).thenReturn(resourceType);

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
      if (isRegistry) {
        biobanksRegType.add(biobank);
        collectionsRegType.add(collection);
      } else {
        biobanksBbType.add(biobank);
        collectionsBbType.add(collection);
      }
    }

    entities.put("biobanks_bb_type", biobanksBbType);
    entities.put("biobanks_reg_type", biobanksRegType);
    entities.put("collections_bb_type", collectionsBbType);
    entities.put("collections_reg_type", collectionsRegType);

    return entities;
  }

  private Query<Entity> getQuery(
      @Nullable String orphaCode,
      @Nullable List<String> resourceType,
      @Nullable List<String> country,
      @Nullable String name,
      @Nullable Integer pageSize,
      @Nullable Integer offset) {
    Query<Entity> q = new QueryImpl<>();
    q.nest();
    if (orphaCode != null) {
      q.in(
          "diagnosis_available.id",
          Collections.singletonList(String.format("ORPHA:%s", orphaCode)));
      if(name != null){
        q.and();
        q.like("name", name);
      }
    }
    else{
      q.like("name", name);
    }
    if (resourceType != null) {
      List<Entity> biobanks = new ArrayList<>();
      if (resourceType.contains(BBMRI_BIOBANK_TYPE)) {
        for (int i = 0; i < 6; i++) {
          Entity e = mock(Entity.class);
          when(e.getIdValue()).thenReturn(String.format("%s%s", BIOBANK_BASE_NAME, i + 1));
          biobanks.add(e);
        }
      }
      if (resourceType.contains(BBMRI_REGISTRY_TYPE)) {
        for (int i = 6; i < 10; i++) {
          Entity e = mock(Entity.class);
          when(e.getIdValue()).thenReturn(String.format("%s%s", BIOBANK_BASE_NAME, i + 1));
          biobanks.add(e);
        }
      }
      q.and();
      q.in("biobank", biobanks);
    }
    if (country != null) {
      q.and();
      q.in("country", Collections.singletonList("IT"));
    }
    q.unnest();

    if (pageSize != null) {
      q.pageSize(pageSize);
    }
    if (offset != null) {
      q.offset(offset);
    }
    return q;
  }

  private Query<Entity> getOrphanetServiceQuery(List<String> orphacodes) {
    Query<Entity> q = new QueryImpl<>(dataService, "eu_bbmri_eric_disease_types");
    q.in("orphanet_exact_matching", orphacodes);

    return q;
  }

  private Query<Entity> getBiobankLookupQuery(List<String> resourceType) {
    Query<Entity> q = new QueryImpl<>(dataService, BIOBANK_ENTITY_ID);
    q.in("ressource_types", resourceType);
    return q;
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

    Query<Entity> findalAllQuery = getQuery(ORPHA_CODE, null, null, null, 5, null);
    Query<Entity> countQuery = getQuery(ORPHA_CODE,null, null, null, null, null);
    Query<Entity> orphanetLookupQuery =
        getOrphanetServiceQuery(Collections.singletonList("ORPHA:" + ORPHA_CODE));

    findalAllQuery.pageSize(5);

    List<Entity> entities = getMockData(5);
    List<Entity> lookupEntities = getOrphanetLookupEntities();
    lenient()
        .when(dataService.findAll(DISEASE_TYPES_ENTITY, orphanetLookupQuery))
        .thenReturn(lookupEntities.stream());
    when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery)).thenReturn((long) resultSize * 2);
    when(dataService.findAll(COLLECTIONS_ENTITY_ID, findalAllQuery)).thenReturn(entities.stream());

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

    Query<Entity> findAllQuery = getQuery(ORPHA_CODE,null, null, null, 5, 5);
    Query<Entity> countQuery = getQuery(ORPHA_CODE,null, null, null, null, null);

    Query<Entity> orphanetLookupQuery =
        getOrphanetServiceQuery(Collections.singletonList("ORPHA:" + ORPHA_CODE));

    findAllQuery.pageSize(5);
    findAllQuery.offset(5);
    List<Entity> entities = getMockData(resultSize);

    List<Entity> lookupCallEntities = getOrphanetLookupEntities();
    lenient()
        .when(dataService.findAll(DISEASE_TYPES_ENTITY, orphanetLookupQuery))
        .thenReturn(lookupCallEntities.stream());
    lenient()
        .when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery))
        .thenReturn((long) resultSize * 2);
    lenient()
        .when(dataService.findAll(COLLECTIONS_ENTITY_ID, findAllQuery))
        .thenReturn(entities.stream());

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

    Query<Entity> findAllQuery = getQuery(ORPHA_CODE,null, null, null, 100, null);
    Query<Entity> countQuery = getQuery(ORPHA_CODE,null, null, null, null, null);

    Query<Entity> orphanetLookupQuery =
        getOrphanetServiceQuery(Collections.singletonList("ORPHA:" + ORPHA_CODE));

    List<Entity> lookupCallEntities = getOrphanetLookupEntities();

    lenient()
        .when(dataService.findAll(DISEASE_TYPES_ENTITY, orphanetLookupQuery))
        .thenReturn(lookupCallEntities.stream());

    when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery)).thenReturn((long) resultSize);
    when(dataService.findAll(COLLECTIONS_ENTITY_ID, findAllQuery)).thenReturn(entities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(URI.create(String.format("%s?orphaCode=%s", BASE_API_URL, ORPHA_CODE))));
    resultActions.andExpect(status().isOk());

    checkContentType(resultActions);
    checkApiVersion(resultActions);
    checkResultData(resultActions, resultSize);
    checkPageData(resultActions, 100, resultSize, 1, 0);
  }

  @Test
  void testGetResourcesByCodeAndName() throws Exception {
    reset(dataService);
    int resultSize = 10;
    List<Entity> entities = getMockData(resultSize);

    Query<Entity> findAllQuery = getQuery(ORPHA_CODE,null, null, "Cell", 100, null);
    Query<Entity> countQuery = getQuery(ORPHA_CODE,null, null, "Cell", null, null);
    Query<Entity> orphanetLookupQuery =
        getOrphanetServiceQuery(Collections.singletonList("ORPHA:" + ORPHA_CODE));

    List<Entity> lookupEntities = getOrphanetLookupEntities();

    lenient()
        .when(dataService.findAll(DISEASE_TYPES_ENTITY, orphanetLookupQuery))
        .thenReturn(lookupEntities.stream());

    when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery)).thenReturn((long) resultSize);
    when(dataService.findAll(COLLECTIONS_ENTITY_ID, findAllQuery)).thenReturn(entities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(URI.create(String.format("%s?orphaCode=%s&name=Cell", BASE_API_URL, ORPHA_CODE))));
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
    HashMap<String, List<Entity>> entities = getMockResourcesSplittedData(resultSize);
    List<Entity> collectionEntities = entities.get("collections_bb_type");
    List<Entity> biobankEntities = entities.get("biobanks_bb_type");

    Query<Entity> findAllQuery =
        getQuery(ORPHA_CODE,Collections.singletonList(BBMRI_BIOBANK_TYPE), null, null, 100, null);
    Query<Entity> countQuery =
        getQuery(ORPHA_CODE,Collections.singletonList(BBMRI_BIOBANK_TYPE), null, null, null, null);
    Query<Entity> biobankTypeQuery =
        getBiobankLookupQuery(Collections.singletonList(BBMRI_BIOBANK_TYPE));
    Query<Entity> orphanetLookupQuery =
        getOrphanetServiceQuery(Collections.singletonList("ORPHA:" + ORPHA_CODE));

    List<Entity> lookupEntities = getOrphanetLookupEntities();
    lenient()
        .when(dataService.findAll(DISEASE_TYPES_ENTITY, orphanetLookupQuery))
        .thenReturn(lookupEntities.stream());

    lenient()
        .when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery))
        .thenReturn((long) resultSize);
    lenient()
        .when(dataService.findAll(COLLECTIONS_ENTITY_ID, findAllQuery))
        .thenReturn(collectionEntities.stream());
    lenient()
        .when(dataService.findAll(BIOBANK_ENTITY_ID, biobankTypeQuery))
        .thenReturn(biobankEntities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(
                URI.create(
                    String.format(
                        "%s?orphaCode=%s&resourceType=%s",
                        BASE_API_URL, ORPHA_CODE, EJPRD_BIOBANK_TYPE))));
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.resourceResponses", hasSize(6)));
  }

  @Test
  void testGetResourceByCodeAndMultipleResourceType() throws Exception {
    reset(dataService);
    int resultSize = 10;
    HashMap<String, List<Entity>> entities = getMockResourcesSplittedData(resultSize);
    List<Entity> collectionEntities = entities.get("collections_bb_type");
    collectionEntities.addAll(entities.get("collections_reg_type"));

    Query<Entity> findAllQuery = getQuery(ORPHA_CODE,null, null, null, 100, null);
    Query<Entity> countQuery = getQuery(ORPHA_CODE,null, null, null, null, null);
    Query<Entity> orphanetLookupQuery =
        getOrphanetServiceQuery(Collections.singletonList("ORPHA:" + ORPHA_CODE));

    List<Entity> lookupEntities = getOrphanetLookupEntities();
    lenient()
        .when(dataService.findAll(DISEASE_TYPES_ENTITY, orphanetLookupQuery))
        .thenReturn(lookupEntities.stream());

    lenient()
        .when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery))
        .thenReturn((long) resultSize);
    lenient()
        .when(dataService.findAll(COLLECTIONS_ENTITY_ID, findAllQuery))
        .thenReturn(collectionEntities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(
                URI.create(
                    String.format(
                        "%s?orphaCode=%s&resourceType=%s,%s",
                        BASE_API_URL, ORPHA_CODE, EJPRD_REGISTRY_TYPE, EJPRD_BIOBANK_TYPE))));
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.resourceResponses", hasSize(10)));
  }

  @Test
  void testGetResourceByCodeAndResourceTypeRegistry() throws Exception {
    reset(dataService);
    int resultSize = 10;
    HashMap<String, List<Entity>> entities = getMockResourcesSplittedData(resultSize);
    List<Entity> collectionEntities = entities.get("collections_reg_type");
    List<Entity> biobankEntities = entities.get("biobanks_reg_type");

    Query<Entity> findAllQuery =
        getQuery(ORPHA_CODE, Collections.singletonList(BBMRI_REGISTRY_TYPE), null, null, 100, null);
    Query<Entity> countQuery =
        getQuery(ORPHA_CODE, Collections.singletonList(BBMRI_REGISTRY_TYPE), null, null, null, null);
    Query<Entity> biobankTypeQuery =
        getBiobankLookupQuery(Collections.singletonList(BBMRI_REGISTRY_TYPE));
    Query<Entity> orphanetLookupQuery =
        getOrphanetServiceQuery(Collections.singletonList("ORPHA:" + ORPHA_CODE));

    List<Entity> lookupEntities = getOrphanetLookupEntities();

    lenient()
        .when(dataService.findAll(DISEASE_TYPES_ENTITY, orphanetLookupQuery))
        .thenReturn(lookupEntities.stream());

    lenient()
        .when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery))
        .thenReturn((long) resultSize);
    lenient()
        .when(dataService.findAll(COLLECTIONS_ENTITY_ID, findAllQuery))
        .thenReturn(collectionEntities.stream());
    lenient()
        .when(dataService.findAll(BIOBANK_ENTITY_ID, biobankTypeQuery))
        .thenReturn(biobankEntities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(
                URI.create(
                    String.format(
                        "%s?orphaCode=%s&resourceType=%s",
                        BASE_API_URL, ORPHA_CODE, EJPRD_REGISTRY_TYPE))));
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.resourceResponses", hasSize(4)));
  }

  @Test
  void testGetResourceByCodeAndResourceTypeParamNotAllowed() throws Exception {
    ResultActions resultActions =
        this.mockMvc.perform(
            get(
                URI.create(
                    String.format(
                        "%s?name=%s&orphaCode=%s&resourceType=fooType",
                        BASE_API_URL, DISEASE_NAME, ORPHA_CODE))));
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  void testGetResourceByCodeAndCountry() throws Exception {
    reset(dataService);
    int resultSize = 10;
    HashMap<String, List<Entity>> entities = getMockResourcesSplittedData(resultSize);
    List<Entity> collectionEntities = entities.get("collections_reg_type");

    Query<Entity> findAllQuery = getQuery(ORPHA_CODE,null, Collections.singletonList("IT"), null, 100, null);
    Query<Entity> countQuery = getQuery(ORPHA_CODE, Collections.singletonList("IT"), null, null, null, null);

    Query<Entity> orphanetLookupQuery =
        getOrphanetServiceQuery(Collections.singletonList("ORPHA:" + ORPHA_CODE));

    List<Entity> lookupEntities = getOrphanetLookupEntities();
    lenient()
        .when(dataService.findAll(DISEASE_TYPES_ENTITY, orphanetLookupQuery))
        .thenReturn(lookupEntities.stream());

    lenient()
        .when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery))
        .thenReturn((long) resultSize);
    lenient()
        .when(dataService.findAll(COLLECTIONS_ENTITY_ID, findAllQuery))
        .thenReturn(collectionEntities.stream());
    ResultActions resultActions =
        this.mockMvc.perform(
            get(URI.create(String.format("%s?orphaCode=%s&country=IT", BASE_API_URL, ORPHA_CODE))));
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.resourceResponses", hasSize(4)));
  }

  @Test
  void testGetResourcesByNameOnly() throws Exception {
    reset(dataService);
    int resultSize = 10;
    List<Entity> entities = getMockData(resultSize);

    Query<Entity> findAllQuery = getQuery(null, null, null, "Cell", 100, null);
    Query<Entity> countQuery = getQuery(null, null, null, "Cell", null, null);

    when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery)).thenReturn((long) resultSize);
    when(dataService.findAll(COLLECTIONS_ENTITY_ID, findAllQuery)).thenReturn(entities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(URI.create(String.format("%s?name=Cell", BASE_API_URL))));
    resultActions.andExpect(status().isOk());

    checkContentType(resultActions);
    checkApiVersion(resultActions);
    checkResultData(resultActions, resultSize);
    checkPageData(resultActions, 100, resultSize, 1, 0);
  }

  @Test
  void testGetResourcesByNameAndCountry() throws Exception {
    reset(dataService);
    int resultSize = 10;
    List<Entity> entities = getMockData(resultSize);

    Query<Entity> findAllQuery = getQuery(null, null, Collections.singletonList("IT"), "Cell", 100, null);
    Query<Entity> countQuery = getQuery(null, null, Collections.singletonList("IT"), "Cell", null, null);

    when(dataService.count(COLLECTIONS_ENTITY_ID, countQuery)).thenReturn((long) resultSize);
    when(dataService.findAll(COLLECTIONS_ENTITY_ID, findAllQuery)).thenReturn(entities.stream());

    ResultActions resultActions =
        this.mockMvc.perform(
            get(URI.create(String.format("%s?name=Cell&country=IT", BASE_API_URL))));
    resultActions.andExpect(status().isOk());

    checkContentType(resultActions);
    checkApiVersion(resultActions);
    checkResultData(resultActions, resultSize);
    checkPageData(resultActions, 100, resultSize, 1, 0);
  }

  @Test
  void testGetResourcesByValidOrphacodeAndEmptyName() throws Exception {
    ResultActions resultActions =
        this.mockMvc.perform(get(URI.create(String.format("%s?orphaCode=%s&name", BASE_API_URL, ORPHA_CODE))));
    resultActions.andExpect(status().isBadRequest());
    //      resultActions.andExpect(jsonPath("$.errors", is("")));
  }

  @Test
  void testGetResourcesByEmptyOrphacodeAndValidName() throws Exception {
    ResultActions resultActions =
        this.mockMvc.perform(get(URI.create(String.format("%s?name=Cell&orphaCode", BASE_API_URL))));
    resultActions.andExpect(status().isBadRequest());
    //      resultActions.andExpect(jsonPath("$.errors", is("")));
  }

}
