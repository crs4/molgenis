package org.molgenis.api.ejprd.externalServices;

import static java.util.Objects.requireNonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class ERDRIResourcesService {
  //private String serviceURI = "http://host.docker.internal:8089/resource"; // mocked service
  private RestTemplate restTemplate = new RestTemplate();
  private final String erdriSourceIdentifier = "id";
  private final String erdriSourceIdentifierValue = "erdri";
  private final String externalSourcesEntity = "eu_bbmri_eric_external_sources";
  private final String ERDRIServiceURIField = "base_uri";

  private DataService dataService;
  public ERDRIResourcesService(DataService dataService) {
    this.dataService = requireNonNull(dataService);
  }

  private String getERDRIServiceURI(){
    //Call dataservice and execute the query to retrieve URI (at the moment mocked service)
    Query<Entity> q = new QueryImpl<>();
    q.nest();
    q.eq(erdriSourceIdentifier, erdriSourceIdentifierValue);
    q.unnest();
    Stream<Entity> entities = dataService.findAll(externalSourcesEntity, q);
    return entities.findFirst().get().getString(ERDRIServiceURIField);
  }

  public JsonObject getERDRIResources(String name) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(String.format(getERDRIServiceURI() + "?name=" + name));
    HttpEntity<?> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        this.restTemplate.getForEntity(builder.toUriString(), String.class);
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

  public CatalogResponse CreateERDRICatalogReponse(JsonObject results) {
    JsonElement resources = results.get("resourceResponses");
    JsonArray resourcesArray = resources.getAsJsonArray();
    List<ResourceResponse> erdriResources = new ArrayList<>();
    String erdriCatalog = "ERDRI";
    String erdriUrl = "https://eu-rd-platform.jrc.ec.europa.eu/erdridor/";
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
    return CatalogResponse.create(erdriCatalog, erdriUrl, erdriResources);
  }

  //  public static void main(String args[]) throws Exception {
  //    ERDRIResourcesService s = new ERDRIResourcesService();
  //    JsonObject resources = s.getERDRIResources("test");
  //    s.CreateERDRICatalogReponse(resources);
  //    //System.out.println(s.getERDRIResources("test"));
  //
  //  }

}
