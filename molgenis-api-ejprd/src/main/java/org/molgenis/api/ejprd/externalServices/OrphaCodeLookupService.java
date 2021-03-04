package org.molgenis.api.ejprd.externalServices;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.springframework.stereotype.Controller;

@Controller
public class OrphaCodeLookupService {

  private final String orphaCodeLookupEntity = "eu_bbmri_eric_disease_types";
  private final String orphaCodeExactMatchingColumn = "orphanet_exact_matching";
  private DataService dataService;

  public OrphaCodeLookupService(DataService dataService) {
    this.dataService = requireNonNull(dataService);
  }

  public List<Entity> getICD10ExactMatchByOrphaCode(List<String> orphaCodes) {
    Query<Entity> q = new QueryImpl<>();
    q.nest();
    q.in(orphaCodeExactMatchingColumn, orphaCodes);
    q.unnest();
    Stream<Entity> entities = dataService.findAll(orphaCodeLookupEntity, q);
    Iterator i = entities.iterator();
    ArrayList matchingEntities = new ArrayList();
    while (i.hasNext()) {
      Entity e = (Entity) i.next();
      matchingEntities.add(e.getString("id"));
    }

    return matchingEntities; // entities.collect(Collectors.toList());
  }
}
