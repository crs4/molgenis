package org.molgenis.api.ejprd.model;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;

public class ExternalResourceRequest extends ResourceRequest {

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code
  @NotNull private String diagnosisAvailable;

  public String getDiagnosisAvailable() {
    return diagnosisAvailable;
  }

  public void setDiagnosisAvailable(String diagnosisAvailable) {
    this.diagnosisAvailable = diagnosisAvailable;
  }

  @AssertFalse(message = "At least one search paramaters must be present")
  private boolean isQueryEmpty() {
    return getDiagnosisAvailable() == null;
  }
}
