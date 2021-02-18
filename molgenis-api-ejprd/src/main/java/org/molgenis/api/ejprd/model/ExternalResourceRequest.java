package org.molgenis.api.ejprd.model;

import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class ExternalResourceRequest extends ResourceRequest {

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code
  @NotEmpty private List<String> diagnosisAvailable;

  @Nullable
  private List<@Pattern(regexp = "(BiobankDataset|PatientRegistryDataset)") String> resourceType;

  public List<String> getDiagnosisAvailable() {
    return diagnosisAvailable;
  }

  public void setDiagnosisAvailable(List<String> diagnosisAvailable) {
    this.diagnosisAvailable = diagnosisAvailable;
  }

  public @Nullable List<String> getResourceType() {
    return resourceType;
  }

  public void setResourceType(@Nullable List<String> resourceType) {
    this.resourceType = resourceType;
  }

  @AssertFalse(message = "At least one search paramaters must be present")
  private boolean isQueryEmpty() {
    return getDiagnosisAvailable() == null;
  }

  @Override
  public String toString() {
    return String.format(
        "ExternalResourceRequest with paramaters:\ndiagnosisAvailable: %s\nresourceType: %s",
        getDiagnosisAvailable(),
        getResourceType() != null ? String.join(",", getResourceType()) : "null");
  }
}
