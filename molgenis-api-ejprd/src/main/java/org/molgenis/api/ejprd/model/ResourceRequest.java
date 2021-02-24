package org.molgenis.api.ejprd.model;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class ResourceRequest {

  // At the moment ORPHA code is expected; TODO: implement lookup in case of IDC10 code
  @NotEmpty(message = "OrphaCode is mandatory")
  private List<String> orphaCode;

  private String name;

  @Nullable
  private List<
          @Pattern(
              regexp = "(BiobankDataset|PatientRegistryDataset)",
              message = "resourceType value not allowed")
          String>
      resourceType;

  @Nullable private List<String> country;

  @Min(value = 0, message = "Skip should be at least 0")
  private Integer skip = 0;

  @Min(value = 1, message = "Limit should be at least 1")
  private Integer limit = 100;

  private boolean queryEmpty;

  private boolean validOffset;

  public List<String> getOrphaCode() {
    return orphaCode;
  }

  public void setOrphaCode(@NotBlank List<String> orphaCode) {
    this.orphaCode = orphaCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public @Nullable List<String> getResourceType() {
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

  public Integer getSkip() {
    return skip;
  }

  public void setSkip(Integer skip) {
    this.skip = skip;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  @AssertTrue(message = "Max offset (limit*skip) is 10000")
  private boolean isValidOffset() {
    // TODO: For some reason max offset performing a query is 10000
    return getLimit() * getSkip() < 10000;
  }

  protected String listToString(@Nullable List<String> toConvert) {
    return toConvert != null ? String.join(",", toConvert) : "null";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceRequest that = (ResourceRequest) o;
    return queryEmpty == that.queryEmpty
        && isValidOffset() == that.isValidOffset()
        && Objects.equals(getOrphaCode(), that.getOrphaCode())
        && Objects.equals(getName(), that.getName())
        && Objects.equals(getResourceType(), that.getResourceType())
        && Objects.equals(getCountry(), that.getCountry())
        && Objects.equals(getSkip(), that.getSkip())
        && Objects.equals(getLimit(), that.getLimit());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getOrphaCode(),
        getName(),
        getResourceType(),
        getCountry(),
        getSkip(),
        getLimit(),
        queryEmpty,
        isValidOffset());
  }
}
