package org.molgenis.api.ejprd.externalServices;

import static java.util.Objects.requireNonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ExternalSourceService {
  private final String externalSourceIdentifierFieldName = "id";
  private final String externalSourcesEntity = "eu_bbmri_eric_external_sources";
  private final String externalSourceURIFieldName = "service_uri";
  // private String serviceURI = "http://host.docker.internal:8089/resource"; // mocked service
  private RestTemplate restTemplate = new RestTemplate();
  private DataService dataService;

  public ExternalSourceService(DataService dataService) {
    // this.externalSourceIdentifierValue = requireNonNull(externalSourceIdentifierValue);
    this.dataService = requireNonNull(dataService);
  }

  public HashMap<String, HashMap<String, String>> getConfiguredExternalSources() {
    Query<Entity> q = new QueryImpl<>();
    q.nest();
    q.unnest();
    Stream<Entity> entities = dataService.findAll(externalSourcesEntity);
    HashMap<String, HashMap<String, String>> configuredEntities = new HashMap<>();
    Iterator<Entity> i = entities.iterator();
    while (i.hasNext()) {
      Entity configuredEntity = i.next();
      HashMap<String, String> entity = new HashMap();
      String entity_id = (String) configuredEntity.get("id");
      entity.put("id", (String) configuredEntity.get("id"));
      entity.put("name", (String) configuredEntity.get("name"));
      entity.put("description", (String) configuredEntity.get("description"));
      entity.put("base_uri", (String) configuredEntity.get("base_uri"));
      entity.put("service_uri", (String) configuredEntity.get("service_uri"));
      configuredEntities.put(entity_id, entity);
    }
    return configuredEntities;
  }

  public JsonObject getExternalResources(String serviceURI, String name) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(String.format(serviceURI + "?name=" + name));
    HttpEntity<?> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        this.restTemplate.getForEntity(builder.toUriString(), String.class);
    System.out.println(response.toString());
    // this.restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, JsonObject.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      Gson gson = new Gson();
      JsonElement element = gson.fromJson(response.getBody(), JsonElement.class);
      JsonObject jsonObjResponse = element.getAsJsonObject();
      return jsonObjResponse;

    } else {
      throw new Exception(
          "No results foiund or error occurred, service returned code"
              + response.getStatusCode().toString());
    }
  }

  public CatalogResponse createExternalServiceCatalogReponse(HashMap source, JsonObject results) {
    JsonElement resources = results.get("resourceResponses");
    JsonArray resourcesArray = resources.getAsJsonArray();
    List<ResourceResponse> erdriResources = new ArrayList<>();
    String catalogName = (String) source.get("name");
    String catalogUrl = (String) source.get("base_uri");
    for (JsonElement resource : resourcesArray) {
      JsonObject res = resource.getAsJsonObject();
      ResourceResponse register =
          ResourceResponse.create(
              res.get("name").getAsString(),
              res.get("url").getAsString(),
              res.get("id").getAsString(),
              res.get("description").getAsString(),
              null,
              null,
              null,
              null);
      erdriResources.add(register);
    }
    return CatalogResponse.create(catalogName, catalogUrl, erdriResources);
  }
}
