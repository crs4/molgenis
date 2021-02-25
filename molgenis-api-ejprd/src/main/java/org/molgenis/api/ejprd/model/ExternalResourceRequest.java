package org.molgenis.api.ejprd.model;

import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;

public class ExternalResourceRequest extends ResourceRequest {

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code
  @NotEmpty private List<String> diagnosisAvailable;

  public List<String> getDiagnosisAvailable() {
    return diagnosisAvailable;
  }

  public void setDiagnosisAvailable(@NotEmpty List<String> diagnosisAvailable) {
    this.diagnosisAvailable = diagnosisAvailable;
  }

  @Override
  public String toString() {
    return String.format(
        "ExternalResourceRequest with paramaters:\n"
            + "diagnosisAvailable: %s\n"
            + "resourceType: %s\n"
            + "country: %s",
        getDiagnosisAvailable(), listToString(getResourceType()), listToString(getCountry()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalResourceRequest that = (ExternalResourceRequest) o;
    return Objects.equals(getDiagnosisAvailable(), that.getDiagnosisAvailable()) && super.equals(o);
  }
}
