package org.molgenis.api.ejprd.model;

import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalResourceRequest extends ResourceRequest {

  private static final Logger LOG = LoggerFactory.getLogger(InternalResourceRequest.class);

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code
  @NotEmpty(message = "OrphaCode is mandatory")
  private List<String> orphaCode;

  @Nullable
  private List<
          @Pattern(
              regexp = "(BiobankDataset|PatientRegistryDataset)",
              message =
                  "Resource Type value not allowed. Allowed values are BiobankDataset and  PatientRegistryDataset")
          String>
      resourceType;

  @Nullable private List<String> country;

  public List<String> getOrphaCode() {
    return orphaCode;
  }

  public void setOrphaCode(@NotBlank List<String> orphaCode) {
    this.orphaCode = orphaCode;
  }

  @Nullable
  public List<String> getResourceType() {
    return resourceType;
  }

  public void setResourceType(@Nullable List<String> resourceType) {
    this.resourceType = resourceType;
  }

  @Nullable
  public List<String> getCountry() {
    return country;
  }

  public void setCountry(@Nullable List<String> country) {
    this.country = country;
  }

  private String listToString(@Nullable List<String> toConvert) {
    return toConvert != null ? String.join(",", toConvert) : "null";
  }

  @Override
  public String toString() {
    return String.format(
        "InternalResourceRequest: orphaCode: %s, resourceType: %s, country: %s, skip: %s, limit: %s",
        listToString(getOrphaCode()),
        listToString(getResourceType()),
        listToString(getCountry()),
        getSkip(),
        getLimit());
  }
}
