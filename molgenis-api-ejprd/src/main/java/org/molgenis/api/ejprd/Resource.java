package org.molgenis.api.ejprd;

import java.util.HashMap;
import java.util.List;

public class Resource {

  private final HashMap<String, String> metadata;
  private String url;
  private String uuid;
  private String name;
  private String description;
  private final HashMap<String, Object> facets;

  Resource(
      String catalogueName,
      String catalogURL,
      String url,
      String uuid,
      String name,
      String description,
      String resourceType,
      List<Object> diagnosesAvailable) {
    metadata = new HashMap<>();
    facets = new HashMap<>();
    setCatalogueName(catalogueName);
    setCatalogueURL(catalogURL);
    setUrl(url);
    setUuid(uuid);
    setName(name);
    setDescription(description);
    setResourceType(resourceType);
    for (Object diagnosis : diagnosesAvailable) {
      addDiagnosisAvailable(diagnosis);
    }
  }

  private void setResourceType(String resourceType) {
    facets.put("resourceType", resourceType);
  }

  private String getResourceType(String resourceType) {
    return (String) facets.get("resourceType");
  }

  public void setCatalogueName(String catalogueName) {
    metadata.put("catalogueMetadata", catalogueName);
  }

  public String getCatalogueName() {
    return metadata.get("catalogueMetadata");
  }

  public void setCatalogueURL(String catalogueURL) {
    metadata.put("catalogueURL", catalogueURL);
  }

  public String getCatalogueURL() {
    return metadata.get("catalogueURL");
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void addDiagnosisAvailable(Object diagnosis) {
    facets.put("diagnosisAvailable", diagnosis);
  }
}
