package org.molgenis.api.ejprd.model;

import javax.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalResourceRequest extends ResourceRequest {

  private static final Logger LOG = LoggerFactory.getLogger(InternalResourceRequest.class);

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code

  @NotBlank(message = "OrphaCode is mandatory")
  private String orphaCode;

  public String getOrphaCode() {
    return orphaCode;
  }

  public void setOrphaCode(String orphaCode) {
    this.orphaCode = orphaCode;
  }

  //  @AssertFalse(message = "At least one search paramaters must be present")
  //  private boolean isQueryEmpty() {
  //    return getOrphaCode() == null && getName() == null;
  //  }
}
