package org.molgenis.api.ejprd;

public class CollectionResponse {

  private String url;
  private String uuid;
  private String name;
  private String description;

  CollectionResponse(String url, String uuid, String name, String description) {
    this.url = url;
    this.uuid = uuid;
    this.name = name;
    this.description = description;
  }
}
