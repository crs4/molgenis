package org.molgenis.api.ejprd.externalServices;

import static java.util.Objects.requireNonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.stream.Stream;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class OrphaCodeLookupService {

  private final String orphaLookupServiceIdentifier = "id";
  private final String orphaLookupServiceIdentifierValue = "orpha";
  private final String orphaLookupServiceEntity = "eu_bbmri_eric_lookup_service";
  private final String orphaLookupServiceURIField = "base_uri";
  // private String serviceURI = "https://api.orphacode.org/EN/ClinicalEntity/";
  private String apiKey = "test";
  private RestTemplate restTemplate = new RestTemplate();
  private DataService dataService;

  public OrphaCodeLookupService(DataService dataService) {
    this.dataService = requireNonNull(dataService);
  }

  private String getOrphaServiceURI() {
    // Call dataservice and execute the query to retrieve URI (at the moment mocked service)
    Query<Entity> q = new QueryImpl<>();
    q.nest();
    q.eq(orphaLookupServiceIdentifier, orphaLookupServiceIdentifierValue);
    q.unnest();
    Stream<Entity> entities = dataService.findAll(orphaLookupServiceEntity, q);
    return entities.findFirst().get().getString(orphaLookupServiceURIField);
  }

  //  public static void main(String args[]) throws Exception {
  //    OrphaCodeLookupService s = new OrphaCodeLookupService();
  //
  //    System.out.println(s.getOrphaCodeByICD10("Q87.4"));
  //    System.out.println(s.getICD10ByOrphaCode("558"));
  //  }

  private String getCodefromResults(JsonObject results, String key) {
    JsonElement references = results.get("References");
    // todo: decide what to do if referens has a length > 1
    JsonArray referencesArray = references.getAsJsonArray();
    return referencesArray.get(0).getAsJsonObject().get(key).getAsString();
  }

  public String getOrphaCodeByICD10(String icd10) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set("apiKey", this.apiKey);
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(String.format(getOrphaServiceURI() + "/ICD10/" + icd10));
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        this.restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

    if (response.getStatusCode().equals(HttpStatus.OK)) {
      // Explore references from JsonObject, if the service has found one
      Gson gson = new Gson();
      JsonElement element = gson.fromJson(response.getBody(), JsonElement.class);
      JsonObject jsonObjResults = element.getAsJsonObject();
      return getCodefromResults(jsonObjResults, "ORPHAcode");
    } else {
      throw new Exception(
          "No results found or error occurred, service returned code"
              + response.getStatusCode().toString());
    }
  }

  public String getICD10ByOrphaCode(String orphaCode) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set("apiKey", this.apiKey);
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(
            String.format(getOrphaServiceURI() + "/orphacode/" + orphaCode + "/ICD10"));

    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        this.restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      Gson gson = new Gson();
      JsonElement element = gson.fromJson(response.getBody(), JsonElement.class);
      JsonObject jsonObjResults = element.getAsJsonObject();
      // Explore references from JsonObject, if the service has found one
      return getCodefromResults(jsonObjResults, "Code ICD10");
    } else {
      throw new Exception(
          "No results foiund or error occurred, service returned code"
              + response.getStatusCode().toString());
    }
  }
}
