package org.molgenis.api.ejprd.service.bbmri;

import org.molgenis.api.ejprd.model.Location;
import org.molgenis.api.ejprd.model.Organisation;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.api.ejprd.service.ResourceMapper;
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
    Entity country = (Entity) entity.get("country");
    String name = String.format("%s - %s", biobank.getString("name"), entity.getString("name"));
    String type = "Biobank";
    String description = entity.getString("description");
    String homepage = url;

    return ResourceResponse.create(
        uuid, type, name, description, homepage, mapOrganisation(biobank, mapLocation(country)));
  }

  public Location mapLocation(Entity country) {
    String id = country.getString("id");
    String name = country.getString("name");
    return Location.create(id, name, null, null);
  }

  public Organisation mapOrganisation(Entity biobank, Location location) {
    return Organisation.create(
        biobank.getString("juridical_person"),
        biobank.getString("juridical_person"),
        null,
        null,
        location);
  }
}
