package org.molgenis.api.ejprd.service;

import java.util.ArrayList;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;

public abstract class QueryBuilder {

  // private String resourceType;

  private String diseaseCode;

  private String diseaseOntology;

  private String diseaseName;

  private String resourceType;

  private ArrayList<String> biobankResources;

  private int pageSize;

  private int offset;

  public abstract Query<Entity> buildCount();

  public abstract Query<Entity> build();

  // public String getResourceType() {
  //  return resourceType;
  // }

  // public QueryBuilder setResourceType(String resourceType) {
  // this.resourceType = resourceType;
  //  return this;
  // }

  public String getDiseaseCode() {
    return diseaseCode;
  }

  public QueryBuilder setDiseaseCode(String diseasesCode) {
    this.diseaseCode = diseasesCode;
    return this;
  }

  public String getDiseaseOntology() {
    return diseaseOntology;
  }

  public QueryBuilder setDiseaseOntology(String diseaseOntology) {
    this.diseaseOntology = diseaseOntology;
    return this;
  }

  public String getDiseaseName() {
    return diseaseName;
  }

  public QueryBuilder setDiseaseName(String diseaseName) {
    this.diseaseName = diseaseName;
    return this;
  }

  public String getResourceType() {
    return resourceType;
  }

  public QueryBuilder setResourceType(String resourceType) {
    this.resourceType = resourceType;
    return this;
  }

  public int getPageSize() {
    return pageSize;
  }

  public QueryBuilder setPageSize(int pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  public int getOffset() {
    return offset;
  }

  public QueryBuilder setOffset(int offset) {
    this.offset = offset;
    return this;
  }
}
