package it.infn.mw.voms.api;

import javax.validation.Valid;

@Valid
public class VOMSRequestDTO {
  
  String fqans;
  
  Long lifetime;
  
  String targets;

  public VOMSRequestDTO() {
    // empty constructor
  }

  public String getFqans() {
    return fqans;
  }

  public void setFqans(String fqans) {
    this.fqans = fqans;
  }

  public Long getLifetime() {
    return lifetime;
  }

  public void setLifetime(Long lifetime) {
    this.lifetime = lifetime;
  }

  public String getTargets() {
    return targets;
  }

  public void setTargets(String targets) {
    this.targets = targets;
  }
}
