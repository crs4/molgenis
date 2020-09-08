package org.molgenis.api.ejprd.externalServices;

import java.util.Collections;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.ResourceResponse;

public class OrphanetResourcesService {

  // At the moment this class has a static implementation, returns an example
  // of an Orphanet response, without calling a service

  public CatalogResponse createOrphanetCatalogReponse() {
    String orphanetName = "Orphanet";
    String orphanetUrl = "https://www.orpha.net/";
    ResourceResponse register3 =
        ResourceResponse.create(
            "Cell line and DNA Biobank from patients affected by genetic diseases",
            "https://www.orpha.net/consor/cgi-bin/ResearchTrials_RegistriesMaterials.php?lng=EN&data_id=46305&RegistryMaterialName=Biobanca-di-linee-cellulari-e-di-DNA-da-pazienti-affetti-da-malattie-genetiche&title=Biobanca%20di%20linee%20cellulari%20e%20di%20DNA%20da%20pazienti%20affetti%20da%20malattie%20genetiche&search=ResearchTrials_RegistriesMaterials_Simple",
            "46305",
            null,
            null,
            null,
            null,
            null);
    CatalogResponse orphanet =
        CatalogResponse.create(orphanetName, orphanetUrl, Collections.singletonList(register3));
    return orphanet;
  }
}
