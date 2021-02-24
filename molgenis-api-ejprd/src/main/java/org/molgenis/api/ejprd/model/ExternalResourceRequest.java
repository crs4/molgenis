package org.molgenis.api.ejprd.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.AssertFalse;

public class ExternalResourceRequest extends ResourceRequest {

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code
  private List<String> diagnosisAvailable;

  public List<String> getDiagnosisAvailable() {
    return diagnosisAvailable;
  }

  public void setDiagnosisAvailable(List<String> diagnosisAvailable) {
    this.diagnosisAvailable = diagnosisAvailable;
    List<String> orphaCodes =
        getDiagnosisAvailable().stream()
            .map(
                oc -> {
                  if (oc.contains("ORPHA:")) {
                    return oc.split(":")[1];
                  }
                  return oc;
                })
            .collect(Collectors.toList());
    setOrphaCode(orphaCodes);
  }

  @AssertFalse(message = "At least one search paramaters must be present")
  private boolean isQueryEmpty() {
    return getDiagnosisAvailable() == null;
  }

  @Override
  public String toString() {
    return String.format(
        "ExternalResourceRequest with paramaters:\ndiagnosisAvailable: %s\nresourceType: %s",
        getDiagnosisAvailable(), listToString(getResourceType()));
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

  @Override
  public int hashCode() {
    return Objects.hash(getDiagnosisAvailable());
  }
}
