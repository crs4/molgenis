package org.molgenis.api.ejprd.externalServices;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class OrphaCodeLookupService {

  private String serviceURI = "https://api.orphacode.org/EN/ClinicalEntity/";
  private String apiKey = "test";
  private RestTemplate restTemplate = new RestTemplate();

  public static void main(String args[]) throws Exception {
    OrphaCodeLookupService s = new OrphaCodeLookupService();

    System.out.println(s.getOrphaCodeByICD10("Q87.4"));
    System.out.println(s.getICD10ByOrphaCode("558"));
  }

  private String getCodefromResults(JsonObject results, String key) {
    JsonElement references = results.get("References");
    // todo: decide what to do if referens has a length > 1
    JsonArray referencesArray = references.getAsJsonArray();
    System.out.println(references.toString());
    return referencesArray.get(0).getAsJsonObject().get(key).getAsString();
  }

  public String getOrphaCodeByICD10(String icd10) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set("apiKey", this.apiKey);
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(String.format(serviceURI + "/ICD10/" + icd10));
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<JsonObject> response =
        this.restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, JsonObject.class);

    if (response.getStatusCode().equals(HttpStatus.OK)) {
      // Explore references from JsonObject, if the service has found one
      JsonObject results = response.getBody();
      System.out.println(results.toString());
      return getCodefromResults(results, "ORPHAcode");
    } else {
      throw new Exception(
          "No results foiund or error occurred, service returned code"
              + response.getStatusCode().toString());
    }
  }

  public String getICD10ByOrphaCode(String orphaCode) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set("apiKey", this.apiKey);
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(
            String.format(serviceURI + "/orphacode/" + orphaCode + "/ICD10"));
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<JsonObject> response =
        this.restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, JsonObject.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      // Explore references from JsonObject, if the service has found one
      JsonObject results = response.getBody();
      return getCodefromResults(results, "Code ICD10");
    } else {
      throw new Exception(
          "No results foiund or error occurred, service returned code"
              + response.getStatusCode().toString());
    }
  }
}
