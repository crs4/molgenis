package org.molgenis.api.ejprd.model;

import java.util.List;

public class ResourceRequest {

  private String name;

  private String orphaCode;

  private List<String> medAreas;

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
}
