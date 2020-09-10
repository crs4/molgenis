package org.molgenis.api.ejprd.model;

import java.util.ArrayList;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;

public class ExternalResourceRequest {

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code
  @NotNull private String diagnosisAvailable;

  private ArrayList<String> externalSources;

  public String getDiagnosisAvailable() {
    return diagnosisAvailable;
  }

  public void setDiagnosisAvailable(String diagnosisAvailable) {
    this.diagnosisAvailable = diagnosisAvailable;
  }

  public ArrayList<String> getExternalSources() {
    return externalSources;
  }

  public void setExternalSources(ArrayList<String> externalResources) {
    this.externalSources = externalResources;
  }

  @AssertFalse(message = "At least one search paramaters must be present")
  private boolean isQueryEmpty() {
    return getDiagnosisAvailable() == null && getExternalSources() == null;
  }
}
