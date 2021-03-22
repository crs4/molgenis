package org.molgenis.api.ejprd.model;

import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import org.molgenis.api.ejprd.validators.OrphaCodeOrNameNotNull;

@OrphaCodeOrNameNotNull
public class InternalResourceRequest extends ResourceRequest {

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code
  //@NotEmpty(message = "OrphaCode is mandatory")
  private List<String> orphaCode;

  public List<String> getOrphaCode() {
    return orphaCode;
  }

  public void setOrphaCode(@NotEmpty List<String> orphaCode) {
    this.orphaCode = orphaCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InternalResourceRequest that = (InternalResourceRequest) o;
    return Objects.equals(getOrphaCode(), that.getOrphaCode()) && super.equals(o);
  }
}
