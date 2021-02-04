package org.molgenis.api.ejprd.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalResourceRequest extends ResourceRequest {

  private static final Logger LOG = LoggerFactory.getLogger(InternalResourceRequest.class);

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code

  @NotBlank(message = "OrphaCode is mandatory")
  private String orphaCode;

  @Pattern(
      regexp = "(PatientRegistryDataset|BiobankDataset)",
      message =
          "Resource Type value not "
              + "allowed. Allowed values are BiobankDataset and  PatientRegistryDataset")
  private String type;

  public String getOrphaCode() {
    return orphaCode;
  }

  public void setOrphaCode(String orphaCode) {
    this.orphaCode = orphaCode;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  //  @AssertFalse(message = "At least one search paramaters must be present")
  //  private boolean isQueryEmpty() {
  //    return getOrphaCode() == null && getName() == null;
  //  }
}
