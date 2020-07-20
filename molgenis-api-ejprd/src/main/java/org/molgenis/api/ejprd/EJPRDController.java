package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.molgenis.api.ApiNamespace;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.security.core.runas.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(EJPRDController.BASE_URI)
public class EJPRDController {
  private static final Logger LOG = LoggerFactory.getLogger(EJPRDController.class);

  static final String BASE_URI = ApiNamespace.API_PATH + "/ejprd";

  private final DataService dataService;

  EJPRDController(DataService dataService) {
    this.dataService = requireNonNull(dataService);
  }

  private static UriComponentsBuilder getBaseUri() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_URI);
  }

  @GetMapping("/collection/")
  @ResponseBody
  @RunAsSystem
  public List<CollectionResponse> getCollections() {

    Stream<Entity> collections = dataService.findAll("eu_bbmri_eric_collections");

    List<CollectionResponse> collectionsResponse = new ArrayList<>();

    Consumer<Entity> collectionsConsumer =
        collection -> {
          collectionsResponse.add(createCollection(collection));
        };
    collections.forEach(collectionsConsumer);
    return collectionsResponse;
  }

  private CollectionResponse createCollection(Entity collection) {
    String subjectIRI = getBaseUri().toUriString();

    String url =
        String.format(
            "%s/menu/main/app-molgenis-app-biobank-explorer/collection/%s",
            subjectIRI, collection.getString("id"));

    String uuid = collection.getString("id");
    Entity biobank = (Entity) collection.get("biobank");
    String name = String.format("%s - %s", biobank.getString("name"), collection.getString("name"));
    String description = collection.getString("description");

    return new CollectionResponse(url, uuid, name, description);
  }
}
