package org.molgenis.api.ejprd;

import org.molgenis.data.Entity;
import org.molgenis.data.Query;

public abstract class QueryBuilder {

  private String diseaseCode;

  private String diseaseOntology;

  private String diseaseName;

  public abstract Query<Entity> build();

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
}
