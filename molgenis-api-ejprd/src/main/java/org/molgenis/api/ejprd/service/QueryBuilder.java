package org.molgenis.api.ejprd.service;

import java.util.List;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;

public abstract class QueryBuilder {

  private List<String> diseaseCode;

  private String diseaseOntology;

  public String name;

  private List<String> resourceType;

  private List<String> country;

  private int pageSize;

  private int offset;

  public abstract Query<Entity> buildCount();

  public abstract Query<Entity> build();

  public List<String> getDiseaseCode() {
    return diseaseCode;
  }

  public QueryBuilder setDiseaseCode(List<String> diseasesCode) {
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

  public String getName() {
    return name;
  }

  public QueryBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public List<String> getResourceType() {
    return resourceType;
  }

  public QueryBuilder setResourceType(List<String> resourceType) {
    this.resourceType = resourceType;
    return this;
  }

  public List<String> getCountry() {
    return country;
  }

  public QueryBuilder setCountry(List<String> country) {
    this.country = country;
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
