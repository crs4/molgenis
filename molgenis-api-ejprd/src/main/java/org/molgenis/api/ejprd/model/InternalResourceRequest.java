package org.molgenis.api.ejprd.model;

import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalResourceRequest extends ResourceRequest {

  private static final Logger LOG = LoggerFactory.getLogger(InternalResourceRequest.class);

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code

  @NotBlank(message = "OrphaCode is mandatory")
  private String orphaCode;

  @Nullable
  private List<
          @Pattern(
              regexp = "(BiobankDataset|PatientRegistryDataset)",
              message =
                  "Resource Type value not allowed. Allowed values are BiobankDataset and  PatientRegistryDataset")
          String>
      resourceType;

  public String getOrphaCode() {
    return orphaCode;
  }

  public void setOrphaCode(String orphaCode) {
    this.orphaCode = orphaCode;
  }

  @Nullable
  public List<String> getResourceType() {
    return resourceType;
  }

  public void setResourceType(@Nullable List<String> resourceType) {
    this.resourceType = resourceType;
  }

  @Override
  public String toString() {
    return String.format(
        "InternalResourceRequest: orphaCode: %s, resourceType: %s, skip: %s, limit: %s",
        getOrphaCode(),
        getResourceType() != null ? String.join(",", getResourceType()) : "null",
        getSkip(),
        getLimit());
  }
}
