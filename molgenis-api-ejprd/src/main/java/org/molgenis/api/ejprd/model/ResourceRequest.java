package org.molgenis.api.ejprd.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

public class ResourceRequest {

  private String name;

  @Min(value = 0, message = "Skip should be at least 0")
  private Integer skip = 0;

  @Min(value = 1, message = "Limit should be at least 1")
  private Integer limit = 100;

  private boolean queryEmpty;

  private boolean validOffset;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
}
