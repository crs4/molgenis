package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.data.Entity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class BBMRIEricResourceMapper implements ResourceMapper {

  private static UriComponentsBuilder getBaseUri() {
    return ServletUriComponentsBuilder.fromCurrentContextPath();
  }

  public ResourceResponse mapEntity(Entity entity) {
    String baseURL = getBaseUri().toUriString();

    String url =
        String.format( // TODO: the path should be dynamic
            "%s/menu/main/app-molgenis-app-biobank-explorer/collection/%s",
            baseURL, entity.getString("id"));

    String uuid = entity.getString("id");
    Entity biobank = (Entity) entity.get("biobank");
    String name = String.format("%s - %s", biobank.getString("name"), entity.getString("name"));
    String description = entity.getString("description");

    return ResourceResponse.create(name, url, uuid, description, null, null, null, null);
  }
}
