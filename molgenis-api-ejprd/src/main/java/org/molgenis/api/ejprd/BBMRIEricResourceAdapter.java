package org.molgenis.api.ejprd;

import java.util.ArrayList;
import java.util.List;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.data.Entity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class BBMRIEricResourceAdapter implements ResourceAdapter {

  private final Entity collection;

  private static final String catalogueName = "BBMRI Directory";
  private static final String resourceType = "biobank";

  BBMRIEricResourceAdapter(Entity bbmriCollection) {
    this.collection = bbmriCollection;
  }

  private static UriComponentsBuilder getBaseUri() {
    return ServletUriComponentsBuilder.fromCurrentContextPath();
  }

  public ResourceResponse createResource() {
    String baseURL = getBaseUri().toUriString();

    String url =
        String.format( // TODO: the path should be dynamic
            "%s/menu/main/app-molgenis-app-biobank-explorer/collection/%s",
            baseURL, collection.getString("id"));

    String uuid = collection.getString("id");
    Entity biobank = (Entity) collection.get("biobank");
    String name = String.format("%s - %s", biobank.getString("name"), collection.getString("name"));
    String description = collection.getString("description");
//    Iterable<Entity> diagnosisAvailable = collection.getEntities("diagnosis_available");
//    List<Object> diagnoses = new ArrayList<>();
//    diagnosisAvailable.forEach(
//        diagnosis -> {
//          diagnoses.add(
//              String.format("%s:%s", diagnosis.getString("ontology"), diagnosis.getString("code")));
//        });

    return ResourceResponse.create(name, url, uuid, description, null, null, null, null);
  }
}
