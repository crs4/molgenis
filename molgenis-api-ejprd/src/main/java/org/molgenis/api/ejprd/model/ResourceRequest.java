package org.molgenis.api.ejprd.model;

import java.util.List;
import javax.validation.constraints.AssertFalse;

public class ResourceRequest {

  private String name;

  private String orphaCode;

  private List<String> medAreas;

  private Integer skip = 0;

  private Integer limit = 100;

  private boolean queryEmpty;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOrphaCode() {
    return orphaCode;
  }

  public void setOrphaCode(String orphaCode) {
    this.orphaCode = orphaCode;
  }

  public List<String> getMedAreas() {
    return medAreas;
  }

  public void setMedAreas(List<String> medAreas) {
    this.medAreas = medAreas;
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

  @AssertFalse(message = "At least one search paramaters must be present")
  private boolean isQueryEmpty() {
    return getName() == null && getOrphaCode() == null;
  }
}