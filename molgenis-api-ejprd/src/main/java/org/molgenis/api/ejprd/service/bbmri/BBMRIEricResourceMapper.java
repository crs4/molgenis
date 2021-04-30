package org.molgenis.api.ejprd.service.bbmri;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.molgenis.api.ejprd.model.Location;
import org.molgenis.api.ejprd.model.Organisation;
import org.molgenis.api.ejprd.model.ResourceResponse;
import org.molgenis.api.ejprd.service.ResourceMapper;
import org.molgenis.data.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class BBMRIEricResourceMapper implements ResourceMapper {
  private static final Logger LOG = LoggerFactory.getLogger(BBMRIEricResourceMapper.class);

  private static final String EJPRD_BIOBANK_TYPE = "BiobankDataset";
  private static final String EJPRD_REGISTRY_TYPE = "PatientRegistryDataset";
  private static final String BBMRI_BIOBANK_TYPE = "BIOBANK";
  private static final String BBMRI_REGISTRY_TYPE = "REGISTRY";

  private static UriComponentsBuilder getBaseUri() {
    return ServletUriComponentsBuilder.fromCurrentContextPath();
  }

  public ResourceResponse mapEntity(Entity entity) {
    String baseURL = getBaseUri().toUriString();

    String homepage =
        String.format( // TODO: the path should be dynamic
            "%s/menu/main/app-molgenis-app-biobank-explorer/#/collection/%s",
            baseURL, URLEncoder.encode(entity.getString("id"), StandardCharsets.UTF_8));

    String uuid = entity.getString("id");
    Entity biobank = (Entity) entity.get("biobank");
    Entity country = (Entity) entity.get("country");
    String name = String.format("%s - %s", biobank.getString("name"), entity.getString("name"));
    Entity ressourceType = (Entity) biobank.get("ressource_types");

    String type =
        ressourceType.getString("id").equals(BBMRI_BIOBANK_TYPE)
            ? EJPRD_BIOBANK_TYPE
            : EJPRD_REGISTRY_TYPE;

    String description = entity.getString("description");

    return ResourceResponse.create(
        uuid, type, name, description, homepage, mapOrganisation(biobank, mapLocation(country)));
  }

  private Location mapLocation(Entity country) {
    String id = country.getString("id");
    String name = country.getString("name");
    return Location.create(id, name, null, null);
  }

  private Organisation mapOrganisation(Entity biobank, Location location) {
    return Organisation.create(
        biobank.getString("juridical_person"),
        biobank.getString("juridical_person"),
        null,
        null,
        location);
  }
}
