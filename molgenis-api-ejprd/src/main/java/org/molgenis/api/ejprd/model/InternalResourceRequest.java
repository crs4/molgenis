package org.molgenis.api.ejprd.model;

import java.util.ArrayList;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;

public class InternalResourceRequest extends ResourceRequest {
  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code

  @NotNull
  private String orphaCode;

  public String getOrphaCode() {
    return orphaCode;
  }

  public void setOrphaCode(String orphaCode) {
    this.orphaCode = orphaCode;
  }

  @AssertFalse(message = "At least one search paramaters must be present")
  private boolean isQueryEmpty() {
    return getOrphaCode() == null;
  }
}
